package org.heatup.controllers;

import lombok.SneakyThrows;
import net.lingala.zip4j.core.ZipFile;
import org.heatup.api.UI.AppManager;
import org.heatup.api.controllers.Controller;
import org.heatup.api.serialized.SerializedObject;
import org.heatup.api.serialized.implementations.SerializedObjectImpl;
import org.heatup.core.Main;
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

    public DownloadController(AppManager manager, ReleaseController releases) {
        this.manager = manager;
        this.releases = releases;
        this.serializedZips = SerializedObjectImpl.<List<Integer>>
                create(FileUtils.path("updates", "zips"), false, new LinkedList<Integer>());
    }

    @Override
    public void start() {
        this.future = manager.getWorker().submit(new Runnable() {
            @Override
            public void run() {
                download();
                unzipall();

                manager.getForm().updateFinished();
            }
        });
    }

    private void download() {
        LinkedBlockingDeque<URL> files = releases.getFiles();
        int increment = 1;
        long downloaded = 0;

        while(true) {
            try {
                URL url = files.poll(1, TimeUnit.SECONDS);

                if(url == null) {
                    if(files.isEmpty()) {
                        manager.getForm().updateFinished();
                        break;
                    } else continue;
                }

                URLConnection connection = url.openConnection();
                File file = new File(url.getPath().substring(1));
                long length = file.length();

                if(connection.getContentLengthLong() == file.length()) {
                    downloadFinished(file);
                    continue;
                }
                else if(file.exists())
                    connection.setRequestProperty("Range", "bytes=" + length + "-");

                FileChannel download = new FileOutputStream(file, file.exists()).getChannel();

                long totalBytes = connection.getContentLengthLong() + length, start = System.nanoTime(),
                        time = System.nanoTime(), between, elapsed, data = 0;
                int percent, totalPercent;

                ReadableByteChannel channel = Channels.newChannel(connection.getInputStream());

                while(download.transferFrom(channel, file.length(), 1024) > 0) {
                    between = System.nanoTime() - time;

                    if(between < 1000000000) continue;

                    length = file.length();
                    elapsed = System.nanoTime() - start;

                    percent = (int) ((double) ((double)length / ((double)totalBytes == 0.0 ? 1.0 : (double)totalBytes) * 100.0));
                    totalPercent = (int) (((double)downloaded / (double)releases.getUpdateLength()) * 100.0);

                    manager.getForm().updateCurrentPercentage(
                            percent,
                            increment,
                            files.size());

                    manager.getForm().updateTotalPercentage(totalPercent,
                            FileUtils.getTimeAsString(((elapsed * totalBytes / length) - elapsed)/1000000),
                            FileUtils.getReadableSize(length - data));

                    time = System.nanoTime();
                    data = length;
                }

                downloadFinished(file);

                download.close();
                channel.close();
                increment++;
            } catch(Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void downloadFinished(File file) {
        int release = Integer.parseInt(file.getName().replace(".zip", ""));
        releases.getSerializedRelease().setObject(release).write();
    }

    private int increment = 1;
    private ZipFile zip;

    @SneakyThrows
    private void unzipall() {
        final List<Integer> zips = serializedZips.get();

        progressFuture = manager.getWorker().submit(new Runnable() {
            @SneakyThrows
            public void run() {
                while(zips.size() >= increment) {
                    Thread.sleep(10);

                    if(zip == null) continue;

                    manager.getForm().updateCompressPercentage(
                            zip.getProgressMonitor().getPercentDone(),
                            increment,
                            zips.size()
                    );
                }
            }
        });

        for(int i: zips) {
            File file = new File(FileUtils.path("updates", String.valueOf(i), ".zip"));

            if(!file.exists()) {
                releases.getFiles().addLast(new URL(
                        FileUtils.path(Main.SERVER, "releases", AppUtils.OS.toString(), i + ".zip")));

                if(!progressFuture.isCancelled())
                    progressFuture.cancel(true);

                download();
                return;
            }

            zip = new ZipFile(file);
            zip.extractAll(new File("").getAbsolutePath());

            zips.remove((Integer) i);
            serializedZips.setObject(zips).write();
            increment++;
        }
}

    @Override
    public void end() {
        if(future != null && !future.isCancelled())
            future.cancel(true);

        if(progressFuture != null && !progressFuture.isCancelled())
            progressFuture.cancel(true);
    }
}
