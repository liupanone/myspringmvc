/**
 * 
 * @Date:2019年10月16日
 * 
 */
package com.qq.springmvc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author liupan
 *
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Controller {

	/**
	 * 该注解的一个属性 <br/>
	 * 也就是contriller的名称 <br/>
	 * 
	 * @return
	 */
	public String value();
}
