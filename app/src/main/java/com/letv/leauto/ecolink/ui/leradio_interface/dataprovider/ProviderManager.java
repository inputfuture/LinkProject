package com.letv.leauto.ecolink.ui.leradio_interface.dataprovider;

import java.util.ArrayList;
import java.util.List;

public class ProviderManager {

    private final List<DataProvider> dataProviders = new ArrayList<DataProvider>();

    private static class InstanceHolder {
        static ProviderManager instance = new ProviderManager();
    }

    private ProviderManager() {
    }

    public static ProviderManager getInstance() {
        return InstanceHolder.instance;
    }

    public void addProvider(DataProvider dataProvider) {
        this.dataProviders.add(dataProvider);
    }

    /**
     * 处理
     * @param scene
     */
    public void handleScene(final Scene scene) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                for (DataProvider p : ProviderManager.this.dataProviders) {
                    if (p.matchScene(scene)) {
                        p.checkToAutoLoadData(scene);
                    }
                }
            }
        }).start();
    }
}
