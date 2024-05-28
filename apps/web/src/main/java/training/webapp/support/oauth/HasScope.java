package training.webapp.support.oauth;

import org.springframework.core.annotation.AliasFor;
import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@PreAuthorize("hasAuthority('SCOPE_{scope}')") // Support for annotation parameters new in Spring Security 6.3
public @interface HasScope {

    @AliasFor("value") String scope() default "";

    @AliasFor("scope") String value() default "";
}