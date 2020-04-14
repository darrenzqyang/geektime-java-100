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
		if (this.dateFormat != null) {
			objectMapper.setDateFormat(this.dateFormat);
		}
		// Method for overriding default locale to use for formatting.
		if (this.locale != null) {
			objectMapper.setLocale(this.locale);
		}
		// Method for overriding default TimeZone to use for formatting.
		if (this.timeZone != null) {
			objectMapper.setTimeZone(this.timeZone);
		}

		// Method for setting {@link AnnotationIntrospector} used by this mapper instance for both serialization and deserialization.
		if (this.annotationIntrospector != null) {
			objectMapper.setAnnotationIntrospector(this.annotationIntrospector);
		}
		// Method for setting custom property naming strategy to use.
		if (this.propertyNamingStrategy != null) {
			objectMapper.setPropertyNamingStrategy(this.propertyNamingStrategy);
		}
		// Method for enabling automatic inclusion of type information, using specified handler object for determining which types this affects, as well as details of how information is embedded.
		if (this.defaultTyping != null) {
			objectMapper.setDefaultTyping(this.defaultTyping);
		}
		// Convenience method, equivalent to calling
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
		// 关闭 FAIL_ON_UNKNOWN_PROPERTIES
		customizeDefaultFeatures(objectMapper);
		this.features.forEach((feature, enabled) -> configureFeature(objectMapper, feature, enabled));

		//Method for configuring {@link HandlerInstantiator} to use for creating instances of handlers (such as serializers, deserializers, type and type id resolvers), given a class.
		if (this.handlerInstantiator != null) {
			objectMapper.setHandlerInstantiator(this.handlerInstantiator);
		}
		else if (this.applicationContext != null) {
			objectMapper.setHandlerInstantiator(
					new SpringHandlerInstantiator(this.applicationContext.getAutowireCapableBeanFactory()));
		}
	}
