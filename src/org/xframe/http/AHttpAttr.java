package org.xframe.http;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.xframe.http.AHttpRequest.AHttpMethod;

/*
 * Used to mark some attribute of HttpRequest
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface AHttpAttr {
    public AHttpMethod method() default AHttpMethod.GET;

    public String charset() default "utf-8";
}
