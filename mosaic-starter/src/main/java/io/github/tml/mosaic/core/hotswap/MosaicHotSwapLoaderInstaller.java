package io.github.tml.mosaic.core.hotswap;

import io.github.tml.mosaic.config.MosaicHotSwapConfig;
import io.github.tml.mosaic.hotSwap.init.MosaicAgentSocketClient;
import io.github.tml.mosaic.util.EnvironmentPathFindUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.Socket;

/**
 * @author welsir
 * @description :
 * @date 2025/6/6
 */
@Component
@Slf4j
public class MosaicHotSwapLoaderInstaller implements ApplicationListener<ApplicationReadyEvent> {

    @Resource
    MosaicHotSwapConfig mosaicHotSwapConfig;

    private Process agentProcess;


    private String getCurrentPid() {
        String name = ManagementFactory.getRuntimeMXBean().getName();
        return name.split("@")[0];
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {

        String pid = getCurrentPid();

        try {

            Class<?> currentClass = Class.forName(MosaicHotSwapLoaderInstaller.class.getName());
            Class<?> agentClass = Class.forName("io.github.tml.mosaic.MosaicAgent");
            String currentPath = EnvironmentPathFindUtil.getJarPath(currentClass);

            String agentPath = EnvironmentPathFindUtil.getJarPath(agentClass);

            log.info("install path: {}", currentPath);
            log.info("agent path: {}", agentPath);

            String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
            ProcessBuilder pb = new ProcessBuilder(
                    javaBin,
                    "-jar", agentPath,
                    pid,
                    agentPath,
                    String.valueOf(mosaicHotSwapConfig.getPort())
            );
            log.info("attach agent to target project, listen port is : {}", mosaicHotSwapConfig.getPort());
            pb.inheritIO();
            agentProcess = pb.start();
            waitForAgentPort("localhost",mosaicHotSwapConfig.getPort(),10000);
            MosaicAgentSocketClient.register(mosaicHotSwapConfig.getPort());
        } catch (IOException | ClassNotFoundException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @PreDestroy
    public void onDestroy() {
        if (agentProcess != null) {
            log.info("Shutting down agent process...");
            agentProcess.destroy(); // 停止 agent 进程
        }
        MosaicAgentSocketClient.close();
    }

    public static void waitForAgentPort(String host, int port, int timeoutMillis) throws InterruptedException {
        int waitTime = 0;
        while (waitTime < timeoutMillis) {
            try (Socket socket = new Socket(host, port)) {
                return;
            } catch (Exception e) {
                Thread.sleep(200); // 每200ms重试一次
                waitTime += 200;
            }
        }
        throw new RuntimeException("Agent端口在 " + timeoutMillis + "ms 内未启动成功");
    }
}