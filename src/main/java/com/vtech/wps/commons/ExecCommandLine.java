package com.vtech.wps.commons;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;

import java.io.IOException;

/**
 * TODO
 *
 * @author houzhiwei
 * @date 2018/5/22 9:32
 */
@Deprecated
public class ExecCommandLine {

    public static void run(String command) throws IOException {
        CommandLine commandLine = CommandLine.parse(command);
        DefaultExecutor executor = new DefaultExecutor();
        executor.execute(commandLine);
    }
}
