/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package red.zyc.desensitization.resolver;

import red.zyc.desensitization.annotation.MapSensitive;
import red.zyc.desensitization.desensitizer.MapDesensitizer;

import java.lang.reflect.AnnotatedType;
import java.util.Map;

/**
 * 泛型{@link Map}对象解析器
 *
 * @author zyc
 */
public class MapFlatTypeResolver implements TypeResolver<Map<String, Object>, AnnotatedType> {

    MapDesensitizer desensitizer = new MapDesensitizer();

    @Override
    public Map<String, Object> resolve(Map<String, Object> value, AnnotatedType annotatedType) {
        MapSensitive annotation = annotatedType.getDeclaredAnnotation(MapSensitive.class);
        Map<String, Object> desensitize = desensitizer.desensitize(value, annotation);
        return desensitize;
    }

    @Override
    public boolean support(Object value, AnnotatedType annotatedType) {
        return value instanceof Map && annotatedType.getDeclaredAnnotation(MapSensitive.class) != null;
    }

    @Override
    public int order() {
        return 1;
    }

}
