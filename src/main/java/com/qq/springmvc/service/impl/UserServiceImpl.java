/**
 * 
 * @Date:2019年10月18日
 * 
 */
package com.qq.springmvc.service.impl;

import com.qq.springmvc.annotation.Qualifier;
import com.qq.springmvc.annotation.Service;
import com.qq.springmvc.dao.UserDao;
import com.qq.springmvc.service.UserService;

/**
 * @author liupan
 *
 */
@Service("userServiceImpl")
public class UserServiceImpl implements UserService {

	@Qualifier("userDaoImpl")
	private UserDao userDao;

	@Override
	public void insert() {
		System.out.println("UserServiceImpl.insert() start");
		userDao.insert();
		System.out.println("UserServiceImpl.insert() end");
	}

}
