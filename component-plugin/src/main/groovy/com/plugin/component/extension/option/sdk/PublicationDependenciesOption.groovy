package com.plugin.component.extension.option.sdk

/**
 * sdk 依赖记录
 * created by yummylau 2019/08/09
 */
class PublicationDependenciesOption {

    List<Object> implementation
    List<Object> compileOnly

    void implementation(Object value) {
        if (implementation == null) {
            implementation = new ArrayList<>()
        }
        implementation.add(value)
    }

    void compileOnly(Object value) {
        if (compileOnly == null) {
            compileOnly = new ArrayList<>()
        }
        compileOnly.add(value)
    }
}
