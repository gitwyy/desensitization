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

package red.zyc.desensitization.annotation;

import red.zyc.desensitization.desensitizer.Condition;
import red.zyc.desensitization.desensitizer.Desensitizer;
import red.zyc.desensitization.desensitizer.MapDesensitizer;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 被该注解标记的对象（Map对象扩展使用）
 * 表明需要对其内部域值进行脱敏处理。
 * 针对数据使用Map集合进行存储，没有Pojo结构的数据
 *
 * @author wangyangyang
 */
@Target({ElementType.FIELD, ElementType.TYPE_USE, ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@SensitiveAnnotation
public @interface MapSensitive {

    /**
     * @return 处理被 {@link MapSensitive}标记的对象脱敏器，注意密码字段类型可能为任意类型，
     * 所以此处的脱敏器支持的类型并没有作限制，可以自定义子类重写默认的处理逻辑。
     */
    Class<? extends Desensitizer<?, MapSensitive>> desensitizer() default MapDesensitizer.class;

    /**
     * @return phoneNo:3,4,*;email:2,3,*
     */
    String config() default "phoneNo:3,4,*;email:2,3,*";

    /**
     * @return 是否需要对目标对象进行脱敏的条件
     */
    Class<? extends Condition<? extends CharSequence>> condition() default AlwaysTrue.class;

    class AlwaysTrue implements Condition<CharSequence> {

        @Override
        public boolean required(CharSequence target) {
            return true;
        }
    }
}
