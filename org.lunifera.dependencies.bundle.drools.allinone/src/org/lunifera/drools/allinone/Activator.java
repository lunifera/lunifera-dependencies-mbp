package org.lunifera.drools.allinone;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.Callable;

import org.drools.KnowledgeBaseFactoryService;
import org.drools.Service;
import org.drools.builder.KnowledgeBuilderFactoryService;
import org.drools.builder.impl.KnowledgeBuilderFactoryServiceImpl;
import org.drools.compiler.BPMN2ProcessProvider;
import org.drools.compiler.DecisionTableProvider;
import org.drools.compiler.ProcessBuilderFactoryService;
import org.drools.decisiontable.DecisionTableProviderImpl;
import org.drools.impl.KnowledgeBaseFactoryServiceImpl;
import org.drools.io.ResourceFactoryService;
import org.drools.io.impl.ResourceFactoryServiceImpl;
import org.drools.marshalling.MarshallerProvider;
import org.drools.marshalling.impl.MarshallerProviderImpl;
import org.drools.marshalling.impl.ProcessMarshallerFactoryService;
import org.drools.runtime.process.ProcessRuntimeFactoryService;
import org.drools.util.ServiceRegistry;
import org.drools.util.ServiceRegistryImpl;
import org.jbpm.bpmn2.BPMN2ProcessProviderImpl;
import org.jbpm.marshalling.impl.ProcessMarshallerFactoryServiceImpl;
import org.jbpm.process.builder.ProcessBuilderFactoryServiceImpl;
import org.jbpm.process.instance.ProcessRuntimeFactoryServiceImpl;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("rawtypes")
public class Activator implements BundleActivator {

	private static BundleContext context;

	protected static final transient Logger logger = LoggerFactory
			.getLogger(Activator.class);

	private ServiceRegistration serviceRegistry;
	private ServiceTracker registryTracker;
	private ServiceTracker marshallerProviderTracker;

	private ServiceRegistration kbuilderReg;
	private ServiceTracker dtableTracker;
	private ServiceTracker bpmn2Tracker;
	private ServiceTracker processRuntimeTracker;
	private ServiceTracker processMarshallerTracker;

	private ServiceRegistration resourceReg;
	private ServiceRegistration kbaseReg;
	private ServiceRegistration marshallerProviderReg;

	private ServiceRegistration kdtableReg;

	private ServiceRegistration bpmn2ProcessReg;

	private ServiceRegistration processBuilderReg;

	private ServiceRegistration processRuntimeReg;
	private ServiceRegistration processRuntimeReg2;

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	@SuppressWarnings("unchecked")
	public void start(BundleContext bc) throws Exception {
		Activator.context = bc;
		logger.info("registering api services");

		this.serviceRegistry = bc.registerService(
				ServiceRegistry.class.getName(),
				ServiceRegistryImpl.getInstance(), new Hashtable());

		this.registryTracker = new ServiceTracker(bc, Service.class.getName(),
				new DroolsServiceTracker(bc, this));

		registryTracker.open();

		this.marshallerProviderTracker = new ServiceTracker(bc,
				MarshallerProvider.class.getName(), new DroolsServiceTracker(
						bc, this));

		this.marshallerProviderTracker.open();

		logger.info("api drools services registered");
		logger.info("registering compiler services");
		this.kbuilderReg = bc.registerService(
				new String[] { KnowledgeBuilderFactoryService.class.getName(),
						Service.class.getName() },
				new KnowledgeBuilderFactoryServiceImpl(), new Hashtable());

		this.dtableTracker = new ServiceTracker(bc, bc.createFilter("(|("
				+ Constants.OBJECTCLASS + "="
				+ DecisionTableProvider.class.getName() + ")("
				+ Constants.OBJECTCLASS + "="
				+ BPMN2ProcessProvider.class.getName() + ") )"),
				new DroolsServiceTracker(bc, this));
		this.dtableTracker.open();

		this.bpmn2Tracker = new ServiceTracker(bc,
				BPMN2ProcessProvider.class.getName(), new DroolsServiceTracker(
						bc, this));
		this.bpmn2Tracker.open();

		this.processRuntimeTracker = new ServiceTracker(bc,
				ProcessRuntimeFactoryService.class.getName(),
				new DroolsServiceTracker(bc, this));
		this.processRuntimeTracker.open();

		this.processMarshallerTracker = new ServiceTracker(bc,
				ProcessMarshallerFactoryService.class.getName(),
				new DroolsServiceTracker(bc, this));
		this.processRuntimeTracker.open();

		logger.info("compiler services registered");

		logger.info("registering core  services");
		this.resourceReg = bc.registerService(
				new String[] { ResourceFactoryService.class.getName(),
						Service.class.getName() },
				new ResourceFactoryServiceImpl(), new Hashtable());

		this.kbaseReg = bc.registerService(
				new String[] { KnowledgeBaseFactoryService.class.getName(),
						Service.class.getName() },
				new KnowledgeBaseFactoryServiceImpl(), new Hashtable());

		this.marshallerProviderReg = bc.registerService(new String[] {
				MarshallerProvider.class.getName(), Service.class.getName() },
				new MarshallerProviderImpl(), new Hashtable());

		logger.info("core services registered");

		logger.info("registering decision tables drools services");
		this.kdtableReg = bc.registerService(
				new String[] { DecisionTableProvider.class.getName(),
						Service.class.getName() },
				new DecisionTableProviderImpl(), new Hashtable());
		logger.info("drools decision tables services registered");

		logger.info("registering bpmn2 process service");
		this.bpmn2ProcessReg = bc.registerService(
				new String[] { BPMN2ProcessProvider.class.getName(),
						Service.class.getName() },
				new BPMN2ProcessProviderImpl(), new Hashtable());
		logger.info("bpmn2 service registered");

		logger.info("registering process builder service");
		this.processBuilderReg = bc.registerService(
				new String[] { ProcessBuilderFactoryService.class.getName(),
						Service.class.getName() },
				new ProcessBuilderFactoryServiceImpl(), new Hashtable());
		logger.info("process builder service registered");

		logger.info("registering process runtime services");
		this.processRuntimeReg = bc.registerService(
				new String[] { ProcessRuntimeFactoryService.class.getName(),
						Service.class.getName() },
				new ProcessRuntimeFactoryServiceImpl(), new Hashtable());
		this.processRuntimeReg2 = bc.registerService(
				new String[] { ProcessMarshallerFactoryService.class.getName(),
						Service.class.getName() },
				new ProcessMarshallerFactoryServiceImpl(), new Hashtable());
		logger.info("process runtime services registered");
		logger.info("all drools services registered");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bc) throws Exception {
		this.serviceRegistry.unregister();
		this.registryTracker.close();
		this.marshallerProviderTracker.close();

		this.kbuilderReg.unregister();
		this.dtableTracker.close();
		this.bpmn2Tracker.close();
		this.processRuntimeTracker.close();
		this.processMarshallerTracker.close();

		this.kbaseReg.unregister();
		this.resourceReg.unregister();
		this.marshallerProviderReg.unregister();

		this.kdtableReg.unregister();

		this.bpmn2ProcessReg.unregister();

		this.processBuilderReg.unregister();

		this.processRuntimeReg.unregister();
		this.processRuntimeReg2.unregister();

		Activator.context = null;
	}

	public static class DroolsServiceTracker implements
			ServiceTrackerCustomizer {

		protected static final transient Logger logger = LoggerFactory
				.getLogger(DroolsServiceTracker.class);

		private BundleContext bc;
		private Activator activator;

		public DroolsServiceTracker(BundleContext bc, Activator activator) {
			this.bc = bc;
			this.activator = activator;
		}

		@SuppressWarnings("unchecked")
		public Object addingService(ServiceReference ref) {
			Service service = (Service) this.bc.getService(ref);
			logger.info("registering api : " + service + " : "
					+ service.getClass().getInterfaces()[0]);

			Dictionary dic = new Hashtable();
			ServiceReference regServiceRef = this.activator.serviceRegistry
					.getReference();
			for (String key : regServiceRef.getPropertyKeys()) {
				dic.put(key, regServiceRef.getProperty(key));
			}
			dic.put(service.getClass().getInterfaces()[0].getName(), "true");
			activator.serviceRegistry.setProperties(dic);

			((ServiceRegistryImpl) bc.getService(regServiceRef))
					.registerLocator(service.getClass().getInterfaces()[0],
							new BundleContextInstantiator(this.bc, ref));

			return service;
		}

		public void modifiedService(ServiceReference arg0, Object arg1) {

		}

		@SuppressWarnings("unchecked")
		public void removedService(ServiceReference ref, Object arg1) {
			Service service = (Service) this.bc.getService(ref);
			logger.info("unregistering : " + service + " : "
					+ service.getClass().getInterfaces()[0]);

			Dictionary dic = new Hashtable();
			ServiceReference regServiceRef = this.activator.serviceRegistry
					.getReference();

			((ServiceRegistryImpl) bc.getService(regServiceRef))
					.unregisterLocator(service.getClass().getInterfaces()[0]);

			for (String key : regServiceRef.getPropertyKeys()) {
				if (!key.equals(service.getClass().getInterfaces()[0].getName())) {
					dic.put(key, regServiceRef.getProperty(key));
				}
			}
			activator.serviceRegistry.setProperties(dic);
		}
	}

	@SuppressWarnings("unchecked")
	public static class BundleContextInstantiator<V> implements Callable<V> {
		private BundleContext bc;
		private ServiceReference ref;

		public BundleContextInstantiator(BundleContext bc, ServiceReference ref) {
			this.bc = bc;
			this.ref = ref;
		}

		public V call() throws Exception {
			return (V) this.bc.getService(this.ref);
		}
	}
}
