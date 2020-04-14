public class ConfigInfo{
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

		if (this.dateFormat != null) {
			objectMapper.setDateFormat(this.dateFormat);
		}
		if (this.locale != null) {
			objectMapper.setLocale(this.locale);
		}
		if (this.timeZone != null) {
			objectMapper.setTimeZone(this.timeZone);
		}

		if (this.annotationIntrospector != null) {
			objectMapper.setAnnotationIntrospector(this.annotationIntrospector);
		}
		if (this.propertyNamingStrategy != null) {
			objectMapper.setPropertyNamingStrategy(this.propertyNamingStrategy);
		}
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

		customizeDefaultFeatures(objectMapper);
		this.features.forEach((feature, enabled) -> configureFeature(objectMapper, feature, enabled));

		if (this.handlerInstantiator != null) {
			objectMapper.setHandlerInstantiator(this.handlerInstantiator);
		}
		else if (this.applicationContext != null) {
			objectMapper.setHandlerInstantiator(
					new SpringHandlerInstantiator(this.applicationContext.getAutowireCapableBeanFactory()));
		}
	}
  }
