/**
 * 
 * @Date:2019年10月18日
 * 
 */
package com.qq.springmvc.dao.impl;

import com.qq.springmvc.annotation.Repository;
import com.qq.springmvc.dao.UserDao;

/**
 * @author liupan
 *
 */
@Repository("userDaoImpl")
public class UserDaoImpl implements UserDao {

	@Override
	public void insert() {
		System.out.println("execute UserDaoImpl.insert()");
	}

}
