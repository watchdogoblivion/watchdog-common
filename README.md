This is a spring based add-on application.
In order to begin using this project, you must first add the root package to your Main Spring main class as below:
```java
	@SpringBootApplication(scanBasePackages = {"com.oblivion.watchdogs.common", "com.my.project"})
```

Then, to have this project scan your project, add your project root to your properties file like below:
root=com.oblivion.watchdogs.rabbit

If you do not add this property, then you will not be benefit from the following:
1. You will not be able to override the Log class.
2. Your package will not be used in the filters in the logging aspect.

To leverage the logging aspect, you must provide an implementation since it is abstract. No overrides are necessary, below will suffice:
```java
	@Component
	public class LoggingAspectImpl extends LoggingAspect {}
```

There are a lot methods and fields you can override for customization.
For instance, it may be of interest to override the method getContextInfo in Log.java. Example below:
```java
	public class CustomLog extends Log{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getContextInfo() {
			return "[Used method to get useful servlet context headers and attributes and placed them here with the service url]";
		}
	}
```
