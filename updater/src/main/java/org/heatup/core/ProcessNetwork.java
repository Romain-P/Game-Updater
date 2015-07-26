package org.heatup.core;

import lombok.Getter;
import lombok.SneakyThrows;
import org.heatup.api.UI.AppManager;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Romain on 22/07/2015.
 */
public class ProcessNetwork {
    private final AppManager manager;
    private final Thread serverThread;
    private Thread processThread;
    private ServerSocket server;
    private int increment;
    private boolean stopAsked, stopProcess;
    private Process process;

    public static final short PORT_BASE = 9500;

    public ProcessNetwork(AppManager manager) {
        this.manager = manager;
        this.increment = 0;
        this.serverThread = new Thread(serverTask());
        this.processThread = new Thread(processTask());

        try {
            this.server = new ServerSocket(PORT_BASE);
            Main.main(new String[] {"network"});
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public int getPort() {
        return PORT_BASE + increment;
    }

    public void start() {
        if(server != null) {
            serverThread.start();
            return;
        }

        try {
            process = new Process(new Socket("127.0.0.1", getPort()));
        } catch(Exception e) {
            increment++;

            try {
                server = new ServerSocket(getPort());
            } catch (Exception f) {
                f.printStackTrace();
            }

            start();
            return;
        }

        process.send(NetworkMessage.CHECK_UPDATER_LAUNCHED);
        processThread.start();
    }

    private Runnable serverTask() {
        return new Runnable() {
            @Override
            public void run() {
                while(!stopAsked) {
                    try {
                        Process newProcess = new Process(server.accept());

                        if (process != null) {
                            newProcess.send(NetworkMessage.CHECK_IN_PROGRESS);
                            newProcess.close();
                            return;
                        }

                        process = newProcess;
                        process.send(NetworkMessage.UPDATER_LAUNCHED_SUCCESS);

                        processThread.start();
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
                stop();
            }
        };
    }

    private Runnable processTask() {
        return new Runnable() {
            @Override @SneakyThrows
            public void run() {
                Thread.sleep(200);
                stopProcess = false;

                while(!stopAsked && !stopProcess) {
                    try {
                        if(process == null) {
                            Thread.sleep(1000);
                            return;
                        }

                        process.receive((NetworkMessage) process.getInput().readObject());
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    public boolean isAlreadyLaunched() {
        return this.server == null;
    }

    public void askStop() {
        stopAsked = true;
    }

    private class Process {
        private final Socket socket;
        private final ObjectOutputStream output;
        @Getter private final ObjectInputStream input;

        @SneakyThrows
        public Process(Socket socket) {
            this.socket = socket;
            this.output = new ObjectOutputStream(socket.getOutputStream());
            this.input = new ObjectInputStream(socket.getInputStream());
        }

        @SneakyThrows
        public void send(NetworkMessage message) {
            output.writeObject(message);
            output.flush();
        }

        public void receive(NetworkMessage message) {
            switch(message) {
                case CHECK_UPDATER_LAUNCHED:
                    send(NetworkMessage.UPDATER_LAUNCHED_SUCCESS);
                    break;
                case UPDATER_LAUNCHED_SUCCESS:
                    send(NetworkMessage.SHOW_UPDATER);
                    askStop();
                    break;
                case CHECK_IN_PROGRESS:
                    askStop();
                    break;
                case SHOW_UPDATER:
                    processThread = new Thread(processTask());
                    stopProcess = true;
                    process = null;
                    manager.getForm().setVisible(true);
                    break;
            }
        }

        @SneakyThrows
        public void close() {
            this.output.close();
            this.input.close();
            this.socket.close();
        }
    }

    @SneakyThrows
    private void stop() {
        if(server != null) {
            if(!server.isClosed())
                server.close();
            server = null;
        }
    }

    private enum NetworkMessage {
        CHECK_UPDATER_LAUNCHED,
        UPDATER_LAUNCHED_SUCCESS,
        CHECK_IN_PROGRESS,
        SHOW_UPDATER
    }
}
