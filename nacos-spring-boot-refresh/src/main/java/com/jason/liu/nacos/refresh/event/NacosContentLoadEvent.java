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

/**
 * 在启动期间Nacos加载内容的事件
 */
public class NacosContentLoadEvent extends NacosConfigEvent {

    private final String content;

    private final NacosRefreshScope nacosRefreshScope;

    public NacosContentLoadEvent(ConfigService configService, String dataId, String groupId, String content, NacosRefreshScope nacosRefreshScope) {
        super(configService, dataId, groupId);
        this.content = content;
        this.nacosRefreshScope = nacosRefreshScope;
    }

    public String getContent() {
        return content;
    }
}
