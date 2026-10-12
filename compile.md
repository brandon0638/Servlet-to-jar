### compiler

```bash
javac -cp "lib/*" -d out src\main\java\com\servlet\FrontControllerServlet.java src\main\java\com\annotation\Controllerako.java src\main\java\com\util\AnnotationUtil.java src\main\java\com\annotation\UrlMapping.java src\main\java\com\annotation\WebAPI.java src\main\java\com\annotation\Param.java src\main\java\com\exception\UrlNotFoundException.java src\main\java\com\listener\AppContextListener.java src\main\java\com\model\ModelAndView.java src\main\java\com\util\RouteMapping.java src\main\java\com\util\UrlMethod.java

```

### creation jar

```bash
jar cf mon-projet.jar -C out .
```

### transformation war

```bash
jar cf test.war -C test .
```