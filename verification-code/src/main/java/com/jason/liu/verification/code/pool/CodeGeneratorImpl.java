package com.jason.liu.verification.code.pool;

import com.jason.liu.verification.code.ICodeGenerator;
import com.jason.liu.verification.code.model.Base64VerificationCode;
import com.jason.liu.verification.code.model.ImageVerificationCode;
import com.jason.liu.verification.code.properties.CodeProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: meng.liu
 * @date: 2021/4/2
 * TODO:
 */
@Slf4j
public class CodeGeneratorImpl implements CommandLineRunner {

    private volatile boolean running;

    /**
     * 缓冲池配置
     */
    private CodeProperty codeProperty;
    /**
     * 缓冲池
     */
    private ICodeBufferPool codeBufferPool;
    /**
     * 生成器
     */
    private ICodeGenerator codeGenerator;
    /**
     * 工作线程
     */
    private VerificationCodeWorker worker;

    public CodeGeneratorImpl(CodeProperty codeProperty,
                             ICodeGenerator codeGenerator,
                             ICodeBufferPool codeBufferPool) {
        this.running = false;
        this.codeProperty = codeProperty;
        this.codeGenerator = codeGenerator;
        this.codeBufferPool = codeBufferPool;
        this.codeBufferPool.setPoolId(codeGenerator.generatorName());
    }

    @Override
    public void run(String... args) throws Exception {
        this.trigger();
    }

    public Base64VerificationCode getBase64() {
        Base64VerificationCode verificationCode = null;
        if (this.codeProperty.getPool().getEnabled() || null != this.codeBufferPool) {
            verificationCode = this.codeBufferPool.get();
        }
        this.trigger();
        if (null == verificationCode) {
            ImageVerificationCode imageVerificationCode = this.codeGenerator.createCode();
            return this.codeGenerator.toBase64(imageVerificationCode);
        }
        return verificationCode;
    }

    public ImageVerificationCode getImage() {
        Base64VerificationCode verificationCode = null;
        if (this.codeProperty.getPool().getEnabled() || null != this.codeBufferPool) {
            verificationCode = this.codeBufferPool.get();
        }
        this.trigger();
        if (null == verificationCode) {
            return this.codeGenerator.createCode();
        }
        return this.codeGenerator.toImage(verificationCode);
    }

    public ICodeGenerator getCodeGenerator() {
        return codeGenerator;
    }

    private void trigger() {
        if (!this.codeProperty.getPool().getEnabled()) {
            return;
        }
        this.startWork();
    }

    private void startWork() {
        if (running) {
            return;
        }
        if (null == this.worker) {
            synchronized (this) {
                if (null == this.worker) {
                    this.worker = new VerificationCodeWorker(this, codeGenerator, codeBufferPool, codeProperty);
                    this.worker.start();
                }
            }
        }
        synchronized (this.worker) {
            if (running) {
                return;
            }
            this.setRunning(true);
            this.worker.notify();
        }
    }

    private synchronized void setRunning(boolean running) {
        this.running = running;
    }

    /**
     * 二维码生产线程
     *
     * @author: meng.liu
     * @date: 2020/7/2
     */
    private static class VerificationCodeWorker extends Thread {

        private CodeGeneratorImpl codePool;

        private CodeProperty codeProperty;

        private ICodeGenerator codeGenerator;

        private ICodeBufferPool codeBufferPool;

        public VerificationCodeWorker(CodeGeneratorImpl codePool,
                                      ICodeGenerator codeGenerator,
                                      ICodeBufferPool codeBufferPool,
                                      CodeProperty codeProperty) {
            this.codePool = codePool;
            this.codeGenerator = codeGenerator;
            this.codeBufferPool = codeBufferPool;
            this.codeProperty = codeProperty;
            this.setName("VerificationWorker");
            this.setDaemon(true);
        }

        @Override
        public void run() {
            int frequency = 10;
            List<Base64VerificationCode> vCodeList = new ArrayList<>();
            while (true) {
                //todo 补充
                while (codeBufferPool.size() < codeProperty.getPool().getMaxSize()) {
                    vCodeList.clear();
                    for (int i = 0; i < frequency; i++) {
                        ImageVerificationCode verificationCode = this.codeGenerator.createCode();
                        vCodeList.add(this.codeGenerator.toBase64(verificationCode));
                    }
                    this.codeBufferPool.store(vCodeList);
                }
                synchronized (this) {
                    try {
                        codePool.setRunning(false);
                        this.wait();
                    } catch (InterruptedException e) {
                        //do nothing
                    }
                }
            }
        }
    }


}
