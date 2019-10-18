/**
 * 
 * @Date:2019年10月18日
 * 
 */
package com.qq.springmvc.servlet;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.qq.springmvc.annotation.Controller;
import com.qq.springmvc.annotation.Qualifier;
import com.qq.springmvc.annotation.Repository;
import com.qq.springmvc.annotation.RequestMapping;
import com.qq.springmvc.annotation.Service;
import com.qq.springmvc.controller.UserController;

/**
 * @author liupan
 *
 */
@WebServlet(name = "dispatcherServlet", urlPatterns = "/*", loadOnStartup = 1, initParams = {
		@WebInitParam(name = "base-package", value = "com.qq.springmvc") })
public class DispatcherServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String basePackage = "";

	private List<String> packageNames = new ArrayList<>();

	private Map<String, Object> instanceMap = new HashMap<>();

	private Map<String, String> nameMap = new HashMap<>();

	private Map<String, Method> urlMethodMap = new HashMap<>();

	private Map<Method, String> methodPackageMap = new HashMap<>();

	@Override
	public void init(ServletConfig config) {
		basePackage = config.getInitParameter("base-package");

		try {
			scanBasePackage(basePackage);
			instance(packageNames);
			springIOC();
			handlerUrlMethodMap();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String uri = req.getRequestURI();
		String contextPath = req.getContextPath();
		String path = uri.replaceAll(contextPath, "");

		Method method = urlMethodMap.get(path);
		if (method != null) {
			String packageName = methodPackageMap.get(method);
			String controllerName = nameMap.get(packageName);

			UserController userController = (UserController) instanceMap.get(controllerName);

			try {
				method.setAccessible(true);
				method.invoke(userController);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	private void scanBasePackage(String basePackage) {
		URL url = this.getClass().getClassLoader().getResource(basePackage.replaceAll("\\.", "/"));
		File basePackageFile = new File(url.getPath());

		System.out.println("scan: " + basePackage);

		File[] childFiles = basePackageFile.listFiles();

		for (File file : childFiles) {
			if (file.isDirectory()) {
				scanBasePackage(basePackage + "." + file.getName());
			} else if (file.isFile()) {
				// 类似 UserServiceImpl.class， 去掉后缀class
				packageNames.add(basePackage + "." + file.getName().split("\\.")[0]);
			}
		}
	}

	private void instance(List<String> packageNames)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		if (packageNames.size() < 1) {
			return;
		}

		for (String name : packageNames) {
			Class c = Class.forName(name);

			if (c.isAnnotationPresent(Controller.class)) {
				Controller controller = (Controller) c.getAnnotation(Controller.class);
				String controllerName = controller.value();

				instanceMap.put(controllerName, c.newInstance());
				nameMap.put(name, controllerName);
				System.out.println("Controller: " + name + ", value: " + controller.value());
			} else if (c.isAnnotationPresent(Service.class)) {
				Service service = (Service) c.getAnnotation(Service.class);
				String serviceName = service.value();

				instanceMap.put(serviceName, c.newInstance());
				nameMap.put(name, serviceName);
				System.out.println("Service : " + name + ", value: " + service.value());
			} else if (c.isAnnotationPresent(Repository.class)) {
				Repository repository = (Repository) c.getAnnotation(Repository.class);
				String repositoryName = repository.value();

				instanceMap.put(repositoryName, c.newInstance());
				nameMap.put(name, repositoryName);
				System.out.println("Repository: " + name + ", value: " + repository.value());
			}

		}
	}

	private void springIOC() throws IllegalArgumentException, IllegalAccessException {
		for (Map.Entry<String, Object> entry : instanceMap.entrySet()) {
			Field[] fields = entry.getValue().getClass().getDeclaredFields();

			for (Field field : fields) {
				if (field.isAnnotationPresent(Qualifier.class)) {
					String name = field.getAnnotation(Qualifier.class).value();
					field.setAccessible(true);
					field.set(entry.getValue(), instanceMap.get(name));
				}
			}
		}
	}

	private void handlerUrlMethodMap() throws ClassNotFoundException {
		if (packageNames.size() < 1) {
			return;
		}

		for (String name : packageNames) {
			Class c = Class.forName(name);

			if (c.isAnnotationPresent(Controller.class)) {
				Method[] methods = c.getMethods();

				StringBuffer baseUrl = new StringBuffer();
				if (c.isAnnotationPresent(RequestMapping.class)) {
					RequestMapping requestMapping = (RequestMapping) c.getAnnotation(RequestMapping.class);
					baseUrl.append(requestMapping.value());
				}

				for (Method method : methods) {
					if (method.isAnnotationPresent(RequestMapping.class)) {
						RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
						baseUrl.append(requestMapping.value());

						urlMethodMap.put(baseUrl.toString(), method);
						methodPackageMap.put(method, name);
					}
				}

			}
		}
	}

}