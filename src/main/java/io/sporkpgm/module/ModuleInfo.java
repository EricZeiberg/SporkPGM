package io.sporkpgm.module;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ModuleInfo {

	public abstract String name();

	public abstract String description();

	public abstract Class<? extends Module>[] requires() default {};

	public boolean multiple() default true;

	public boolean listener() default true;

}
