/**
 * 
 * @Date:2019年10月18日
 * 
 */
package com.qq.springmvc.controller;

import com.qq.springmvc.annotation.Controller;
import com.qq.springmvc.annotation.Qualifier;
import com.qq.springmvc.annotation.RequestMapping;
import com.qq.springmvc.service.UserService;

/**
 * @author liupan
 *
 */
@Controller("userController")
@RequestMapping("/user")
public class UserController {

	@Qualifier("userServiceImpl")
	private UserService userService;

	@RequestMapping("/insert")
	public void insert() {
		userService.insert();
	}

}
