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

package red.zyc.desensitization.desensitizer;

import red.zyc.desensitization.annotation.MapSensitive;
import red.zyc.desensitization.support.InstanceCreators;
import red.zyc.desensitization.util.ReflectionUtil;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 这个是自定义的游离 Desensitizer (Map.Entry)
 *
 * @author wangyangyang
 * @date 2021-07-31 10:52:15
 **/
public class MapDesensitizer implements Desensitizer<Map<String, Object>, MapSensitive> {


    @Override
    public Map<String, Object> desensitize(Map<String, Object> target, MapSensitive annotation) {
        String config = annotation.config();
        Map<String, String> conf = Arrays.stream(config.split(";"))
                .collect(Collectors.toMap(s -> s.split(":")[0], s -> s.split(":")[1]));
        Set<String> targetKeys = target.keySet();
        Set<String> confKeys = conf.keySet();
        Map<String, Object> result = InstanceCreators.getInstanceCreator(ReflectionUtil.getClass(target)).create();
        result.putAll(target);
        confKeys.retainAll(targetKeys);
        confKeys.parallelStream().forEach(k -> {
            String v = result.get(k).toString();
            if (isNotEmptyString(v)) {
                String c = conf.get(k);
                String[] splitConf = c.split(",");
                Integer startOffset = Integer.valueOf(splitConf[0]);
                Integer endOffset = Integer.valueOf(splitConf[1]);
                char placeHolder = splitConf[2].charAt(0);
                char[] desensitize = desensitize(v, startOffset, endOffset, placeHolder);
                result.put(k, new String(desensitize));
            }

        });

        return result;
    }

    /**
     * 基于位置偏移脱敏
     *
     * @param target      目标字符序列对象
     * @param start       敏感信息在原字符序列中的起始偏移
     * @param end         敏感信息在原字符序列中的结束偏移
     * @param placeholder 敏感信息替换后的占位符
     * @return 脱敏后的新字符序列对象的字符数组
     */
    private char[] desensitize(String target, int start, int end, char placeholder) {
        check(start, end, target);
        char[] chars = chars(target);
        replace(chars, start, target.length() - end, placeholder);
        return chars;
    }

    /**
     * 替换字符序列中的敏感信息
     *
     * @param chars       字符序列对应的字符数组
     * @param start       敏感信息在字符序列中的起始索引
     * @param end         敏感信息在字符序列中的结束索引
     * @param placeholder 用来替换敏感字符的占位符
     */
    private void replace(char[] chars, int start, int end, char placeholder) {
        while (start < end) {
            chars[start++] = placeholder;
        }
    }

    /**
     * 将字符序列转换为字符数组
     *
     * @param target 字符序列对象
     * @return 字符序列对象所代表的字符数组
     */
    private char[] chars(String target) {
        char[] chars = new char[target.length()];
        IntStream.range(0, target.length()).forEach(i -> chars[i] = target.charAt(i));
        return chars;
    }

    /**
     * 校验起始偏移和结束偏移的合法性
     *
     * @param startOffset 敏感信息在原字符序列中的起始偏移
     * @param endOffset   敏感信息在原字符序列中的结束偏移
     * @param target      原字符序列
     */
    private void check(int startOffset, int endOffset, String target) {
        if (startOffset < 0 ||
                endOffset < 0 ||
                startOffset + endOffset > target.length()) {
            throw new IllegalArgumentException(String.format("startOffset: %s, endOffset: %s, target: %s",
                    startOffset, endOffset, target));
        }
    }

    /**
     * 判断字符串是否不为空字符串
     *
     * @param string 字符串对象
     * @return 字符串是否不为空字符串
     */
    private boolean isNotEmptyString(String string) {
        return !"".equals(string);
    }

}
