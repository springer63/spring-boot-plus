package com.github.boot.framework.support.mq;

/**
 * 消费模式
 * Created by cjh on 2017/8/7.
 */
public enum MessageModel {

    /**
     * 广播模式
     */
    BROADCASTING(1),

    /**
     * 集群模式
     */
    CLUSTERING(2);

    private int model;

    MessageModel(int model) {
        this.setModel(model);
    }

	public int getModel() {
		return model;
	}

	public void setModel(int model) {
		this.model = model;
	}
}
