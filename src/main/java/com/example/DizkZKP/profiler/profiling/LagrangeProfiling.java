package com.example.DizkZKP.profiler.profiling;

import com.example.DizkZKP.algebra.fft.DistributedFFT;
import com.example.DizkZKP.algebra.fft.SerialFFT;
import com.example.DizkZKP.algebra.curves.barreto_naehrig.bn254a.BN254aFields.BN254aFr;
import com.example.DizkZKP.configuration.Configuration;

public class LagrangeProfiling {

    public static void serialLagrangeProfiling(final Configuration config, final long size) {
        final BN254aFr fieldFactory = new BN254aFr(2L);
        final SerialFFT<BN254aFr> domain = new SerialFFT<>(size, fieldFactory);

        config.setContext("Lagrange-Serial");
        config.beginRuntimeMetadata("Size", size);

        config.beginLog("Lagrange");
        config.beginRuntime("Lagrange");
        domain.lagrangeCoefficients(fieldFactory);
        config.endRuntime("Lagrange");
        config.endLog("Lagrange");

        config.writeRuntimeLog(config.context());
    }

    public static void distributedLagrangeProfiling(final Configuration config, final long size) {
        final BN254aFr fieldFactory = new BN254aFr(2L);

        config.setContext("Lagrange");
        config.beginRuntimeMetadata("Size", size);

        config.beginLog("Lagrange");
        config.beginRuntime("Lagrange");
        DistributedFFT.lagrangeCoeffs(fieldFactory, size, config).count();
        config.endRuntime("Lagrange");
        config.endLog("Lagrange");

        config.writeRuntimeLog(config.context());
    }
}
