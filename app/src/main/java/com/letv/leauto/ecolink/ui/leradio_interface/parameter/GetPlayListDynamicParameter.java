package com.letv.leauto.ecolink.ui.leradio_interface.parameter;


import com.letv.leauto.ecolink.ui.leradio_interface.baseparameter.HttpBaseParameter;
import java.util.Map;

/**
 * Created by Han on 16/7/15.
 */
public class GetPlayListDynamicParameter extends HttpBaseParameter {
    private static final long serialVersionUID = 1L;

    private Map<String, String> queris;

    public Map<String, String> getQueris() {
        return this.queris;
    }

    public void setQueris(Map<String, String> queris) {
        this.queris = queris;
    }

    @Override
    public HttpBaseParameter combineParams() {
        super.combineParams();
        if (this.queris != null) {
            for (final Map.Entry<String, String> entry : this.queris.entrySet()) {
                this.put(entry.getKey(), entry.getValue());
            }
        }
        return this;
    }


}
