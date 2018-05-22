package com.vtech.test;

import org.apache.commons.exec.*;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * TODO
 *
 * @author houzhiwei
 * @date 2018/5/22 9:38
 */
public class Command {

    String baseInput = Paths.get("src", "test", "resources", "data").toString();
    String tif = "\\dem_tx.tif";
    String in = baseInput + tif;
    String baseOutput = Paths.get("src", "test", "resources", "output").toString();

    @Test
    public void command1() throws IOException {
        String command = "gdalinfo " + in;
        CommandLine commandLine = CommandLine.parse(command);
        DefaultExecutor executor = new DefaultExecutor();
        int exitVal = executor.execute(commandLine);
        // 正常则为0
        System.out.println(exitVal);
    }

    @Test
    public void command2() throws IOException, InterruptedException {
        CommandLine commandLine = new CommandLine("gdalinfo");
        commandLine.addArgument(in);
//        非阻塞方式执行进程
        DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
        Executor executor = new DefaultExecutor();
//        后台打印，

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        executor.setStreamHandler(new PumpStreamHandler(out));
        executor.execute(commandLine, resultHandler);
        resultHandler.waitFor();
        System.out.println(resultHandler.getExitValue());
        System.out.println(out.toString("gbk"));
    }

    @Test
    public void command3() throws IOException, InterruptedException {
        String filename = "\\dem_tx";
        CommandLine commandLine = new CommandLine("mpiexec");
        commandLine.addArgument("-n");
        commandLine.addArgument("8");
        commandLine.addArgument("PitRemove");
        commandLine.addArgument("-z");
        commandLine.addArgument(in);
        commandLine.addArgument("-fel");
        commandLine.addArgument(baseOutput + "\\dem_tx_fel.tif");
//        非阻塞方式执行进程
        DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
        Executor executor = new DefaultExecutor();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        executor.setStreamHandler(new PumpStreamHandler(out));
        executor.execute(commandLine, resultHandler);
        resultHandler.waitFor();
        System.out.println(resultHandler.getExitValue());
        System.out.println(out.toString("gbk"));
    }
}
