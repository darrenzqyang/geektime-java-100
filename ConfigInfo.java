public void configure(ObjectMapper objectMapper) {
		Assert.notNull(objectMapper, "ObjectMapper must not be null");

		if (this.findModulesViaServiceLoader) {
			objectMapper.registerModules(ObjectMapper.findModules(this.moduleClassLoader));
		}
		else if (this.findWellKnownModules) {
			registerWellKnownModulesIfAvailable(objectMapper);
		}

		if (this.modules != null) {
			objectMapper.registerModules(this.modules);
		}
		if (this.moduleClasses != null) {
			for (Class<? extends Module> module : this.moduleClasses) {
				objectMapper.registerModule(BeanUtils.instantiateClass(module));
			}
		}
		// Method for configuring the default to use when serializing time values as Strings, and deserializing from JSON Strings.
                // 用于配置将时间值序列化为字符串时使用的默认值，以及从JSON字符串反序列化时使用的默认值。
		if (this.dateFormat != null) {
			objectMapper.setDateFormat(this.dateFormat);
		}
		// Method for overriding default locale to use for formatting.
	        // 方法重写用于格式化的默认语言环境
		if (this.locale != null) {
			objectMapper.setLocale(this.locale);
		}
		// Method for overriding default TimeZone to use for formatting.
		// 方法重写用于格式化的默认时区。
		if (this.timeZone != null) {
			objectMapper.setTimeZone(this.timeZone);
		}

		// Method for setting {@link AnnotationIntrospector} used by this mapper instance for both serialization and deserialization.
		// 用于设置此映射程序实例用于序列化和反序列化的{@link注释内省器}的方法。
		if (this.annotationIntrospector != null) {
			objectMapper.setAnnotationIntrospector(this.annotationIntrospector);
		}
		// Method for setting custom property naming strategy to use.
		// 方法设置要使用的自定义属性命名策略。
		if (this.propertyNamingStrategy != null) {
			objectMapper.setPropertyNamingStrategy(this.propertyNamingStrategy);
		}
		// Method for enabling automatic inclusion of type information, using specified handler object for determining which types this affects, as well as details of how information is embedded.
		// 用于启用类型信息的自动包含，使用指定的处理程序对象确定哪些类型受此影响，以及信息如何嵌入的详细信息。
		if (this.defaultTyping != null) {
			objectMapper.setDefaultTyping(this.defaultTyping);
		}
		if (this.serializationInclusion != null) {
			objectMapper.setSerializationInclusion(this.serializationInclusion);
		}

		if (this.filters != null) {
			objectMapper.setFilterProvider(this.filters);
		}

		this.mixIns.forEach(objectMapper::addMixIn);

		if (!this.serializers.isEmpty() || !this.deserializers.isEmpty()) {
			SimpleModule module = new SimpleModule();
			addSerializers(module);
			addDeserializers(module);
			objectMapper.registerModule(module);
		}

		this.visibilities.forEach(objectMapper::setVisibility);

		// Any change to this method should be also applied to spring-jms and spring-messaging; MappingJackson2MessageConverter default constructors
		// 对该方法的任何更改也应该应用于spring-jms和spring-messaging;MappingJackson2MessageConverter默认构造函数
		// 关闭 FAIL_ON_UNKNOWN_PROPERTIES
		customizeDefaultFeatures(objectMapper);
		this.features.forEach((feature, enabled) -> configureFeature(objectMapper, feature, enabled));

		// Method for configuring {@link HandlerInstantiator} to use for creating instances of handlers (such as serializers, deserializers, type and type id resolvers), given a class.
		// 配置{@link HandlerInstantiator}用于创建处理程序实例(如序列化器、反序列化器、类型和类型id解析器)的方法。
		if (this.handlerInstantiator != null) {
			objectMapper.setHandlerInstantiator(this.handlerInstantiator);
		}
		else if (this.applicationContext != null) {
			objectMapper.setHandlerInstantiator(
					new SpringHandlerInstantiator(this.applicationContext.getAutowireCapableBeanFactory()));
		}
	}
