package com.github.boot.framework.support.mq.onsmq;

import com.github.boot.framework.support.mq.AbstractMessage;

/**
 * 定时消息
 * Created by cjh on 2017/3/29.
 */
public abstract class ScheduledMessage extends AbstractMessage {

	private static final long serialVersionUID = -398756544045530216L;
	
	/**
     * 消息投递时间（未来时间的时间戳）
     */
    private Long deliverTime;

    public Long getDeliverTime() {
        return deliverTime;
    }

    public void setDeliverTime(Long deliverTime) {
        this.deliverTime = deliverTime;
    }
}
