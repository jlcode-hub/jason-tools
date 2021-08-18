/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jason.liu.nacos.refresh.event;

import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.spring.context.event.config.NacosConfigEvent;
import com.jason.liu.nacos.refresh.annotation.NacosRefreshScope;

import java.util.EventObject;

/**
 * {@link NacosRefreshScope} Bean Bound {@link EventObject event}.
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 0.1.0
 */
public class NacosRefreshScopePropertiesBeanBoundEvent extends NacosConfigEvent {

	private final Object bean;

	private final String beanName;

	private final NacosRefreshScope nacosRefreshScope;

	private final String content;

	/**
	 * @param configService     Nacos {@link ConfigService}
	 * @param dataId            data ID
	 * @param groupId           group ID
	 * @param bean              annotated {@link NacosRefreshScope} bean
	 * @param beanName          the name of annotated {@link NacosRefreshScope} bean
	 * @param nacosRefreshScope {@link NacosRefreshScope} object
	 * @param content           the Nacos content for binding
	 */
	public NacosRefreshScopePropertiesBeanBoundEvent(ConfigService configService,
													 String dataId, String groupId, Object bean, String beanName,
													 NacosRefreshScope nacosRefreshScope, String content) {
		super(configService, dataId, groupId);
		this.bean = bean;
		this.beanName = beanName;
		this.nacosRefreshScope = nacosRefreshScope;
		this.content = content;
	}

	public Object getBean() {
		return bean;
	}

	public String getBeanName() {
		return beanName;
	}

	public NacosRefreshScope getNacosRefreshScope() {
		return nacosRefreshScope;
	}

	public String getContent() {
		return content;
	}
}
