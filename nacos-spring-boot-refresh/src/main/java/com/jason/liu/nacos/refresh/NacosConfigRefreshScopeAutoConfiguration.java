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
package com.jason.liu.nacos.refresh;

import com.alibaba.boot.nacos.config.NacosConfigConstants;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Nacos Config Auto {@link Configuration}
 *
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
@ConditionalOnProperty(name = NacosConfigConstants.ENABLED, matchIfMissing = true)
@ConditionalOnClass(name = "org.springframework.boot.context.properties.bind.Binder")
@Import(value = {NacosRefreshScopeConfigBeanDefinitionRegistrar.class})
public class NacosConfigRefreshScopeAutoConfiguration {

}
