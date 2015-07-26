package org.heatup.controllers;

import lombok.SneakyThrows;
import net.lingala.zip4j.core.ZipFile;
import org.heatup.api.UI.AppManager;
import org.heatup.api.controllers.Controller;
import org.heatup.api.serialized.SerializedObject;
import org.heatup.api.serialized.implementations.SerializedObjectImpl;
import org.heatup.core.UpdateManager;
import org.heatup.utils.AppUtils;
import org.heatup.utils.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

/**
 * Created by romain on 16/05/2015.
 */
public class DownloadController implements Controller{
    private final AppManager manager;
    private final ReleaseController releases;
    private Future<?> future, progressFuture;
    private final SerializedObject<List<Integer>> serializedZips;
    private boolean stopRequested;

    public DownloadController(AppManager manager, ReleaseController releases) {
        this.manager = manager;
        this.releases = releases;
        this.serializedZips = SerializedObjectImpl.<List<Integer>>
                create(FileUtils.path(".updates", "zips"), false, new LinkedList<Integer>());
    }

    @Override
    public void start() {
        this.future = manager.getWorker().submit(new Runnable() {
            @Override
            public void run() {
                launchUpdates();
            }
        });
    }

    private void launchUpdates() {
        download();
        unzipall();

        manager.getForm().updateFinished();
    }

    private FileChannel download = null;
    private ReadableByteChannel channel = null;

    private void download() {
        LinkedBlockingDeque<URL> files = releases.getFiles();
        int increment = 1;
        long downloaded = 0;
        long start = System.nanoTime();

        while(true) {
            try {
                URL url = files.poll(2, TimeUnit.SECONDS);

                if(url == null) {
                    if(files.isEmpty()) {
                        manager.getForm().updateFinished();
                        break;
                    } else continue;
                }

                URLConnection connection = url.openConnection();

                String path = url.getFile();

                File file = new File(FileUtils.path(
                        ".updates",
                        String.valueOf(path.substring(path.lastIndexOf('/')+1, path.lastIndexOf('.'))))+".zip");

                long length = file.length();

                connection.setRequestProperty("Range", "bytes=" + length + "-");
                connection.setDoInput(true);
                connection.setDoOutput(true);

                if(connection.getContentLengthLong() == file.length()) {
                    downloadFinished(file);
                    continue;
                }

                file.getParentFile().mkdirs();

                this.download = new FileOutputStream(file, file.exists()).getChannel();

                long totalBytes = connection.getContentLengthLong() + length,
                        time = System.nanoTime(), between, elapsed, data = 0, updated;
                int percent, totalPercent;

                this.channel = Channels.newChannel(connection.getInputStream());

                while(download.transferFrom(channel, file.length(), 1024) > 0) {
                    if(stopRequested) {
                        stopRequested();
                        return;
                    }

                    between = System.nanoTime() - time;

                    if(between < 1000000000) continue;

                    length = file.length();
                    elapsed = System.nanoTime() - start;
                    updated = releases.getUpdateLength();

                    percent = (int) ((double) ((double)length / ((double)totalBytes == 0.0 ? 1.0 : (double)totalBytes) * 100.0));
                    totalPercent = (int) ((double) ((double)downloaded / ((double)updated) * 100.0));

                    manager.getForm().updateCurrentPercentage(
                            percent,
                            increment,
                            releases.getUpdateSteps());

                    manager.getForm().updateTotalPercentage(
                            totalPercent,
                            FileUtils.getTimeAsString(((elapsed * updated / (downloaded == 0 ? 1 : downloaded)) - elapsed) / 1000000),
                            FileUtils.getReadableSize(length - data));

                    downloaded += length - data;
                    time = System.nanoTime();
                    data = length;
                }

                downloadFinished(file);

                download.close();
                channel.close();
                increment++;
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void downloadFinished(File file) {
        int release = Integer.parseInt(file.getName().replace(".zip", ""));
        serializedZips.get().add(release);
        serializedZips.write();
        releases.getSerializedRelease().setObject(release).write();
    }

    private int increment = 1;
    private ZipFile zip;


    private void unzipall() {
        try {
            final List<Integer> zips = new LinkedList<>(serializedZips.get());
            final int zipLength = zips.size();

            progressFuture = manager.getWorker().submit(new Runnable() {
                @SneakyThrows
                public void run() {
                    while (zips.size() >= increment) {
                        Thread.sleep(10);

                        if (zip == null) continue;

                        manager.getForm().updateCompressPercentage(
                                zip.getProgressMonitor().getPercentDone(),
                                increment,
                                zipLength
                        );
                    }
                }
            });

            for (int i : serializedZips.get()) {
                if (stopRequested) {
                    stopRequested();
                    return;
                }

                File file = new File(FileUtils.path(".updates", i+ ".zip"));

                if (!file.exists()) {
                    releases.getFiles().addLast(new URL(
                            UpdateManager.SERVER + "/releases/" + AppUtils.OS.toString() + "/" + i + ".zip"));

                    if (!progressFuture.isCancelled())
                        progressFuture.cancel(true);

                    launchUpdates();
                    return;
                }

                zip = new ZipFile(file);
                zip.extractAll(new File("").getAbsolutePath());

                zips.remove((Integer) i);
                serializedZips.setObject(zips).write();
                increment++;
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void end() {
        this.stopRequested = true;
    }

    public void stopRequested() {
        try {
            if(download != null)
                download.close();
            this.download = null;
        } catch(Exception e) {}

        try {
            if(channel != null)
                this.channel.close();
            this.channel = null;
        } catch(Exception e) {}

        if(future != null && !future.isCancelled())
            future.cancel(true);

        if(progressFuture != null && !progressFuture.isCancelled())
            progressFuture.cancel(true);
    }
}
