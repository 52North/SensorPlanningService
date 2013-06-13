package org.n52.sps.sensor.cite;

import static org.junit.Assert.*;

import net.opengis.ows.x11.ServiceReferenceType;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.junit.Before;
import org.junit.Test;
import org.n52.sps.sensor.model.ResultAccessDataServiceReference;
import org.n52.sps.sensor.model.SensorTask;
import org.n52.sps.service.admin.MissingSensorInformationException;
import org.n52.sps.service.core.SensorInstanceProvider;
import org.n52.testing.utilities.FileContentLoader;
import org.x52North.schemas.sps.v2.SensorInstanceDataDocument;
import org.x52North.schemas.sps.v2.SensorInstanceDataDocument.SensorInstanceData;

public class CiteTaskResultAccessTest {

    private CiteTaskResultAccess citeTaskResultAccess;
    private String procedure;
    private String taskId;

    @Before
    public void setUp() throws Exception {
        XmlObject file = FileContentLoader.loadXmlFileViaClassloader("/files/sensorInstanceData.xml", getClass());
        SensorInstanceDataDocument.Factory.newInstance();
        SensorInstanceDataDocument instanceDataDoc = SensorInstanceDataDocument.Factory.parse(file.newInputStream());
        SensorInstanceData sensorInstanceData = instanceDataDoc.getSensorInstanceData();
        SensorInstanceProviderSeam sensorInstanceProviderSeam = new SensorInstanceProviderSeam();
        ResultAccessDataServiceReference reference = sensorInstanceProviderSeam.createResultAccess(sensorInstanceData);

        procedure = "http://host.tld/procedure1/";
        taskId = "http://host.tld/procedure1/tasks/1";
        SensorTask validTask = new SensorTask(taskId, procedure);
        citeTaskResultAccess = new CiteTaskResultAccess(validTask, reference);
    }

    @Test
    public void testCreateRequestMessageReference() throws Exception {
        String request = citeTaskResultAccess.createRequest();
        assertNotNull(XmlObject.Factory.parse(request));
    }
    
    @Test
    public void testCreateServiceReference() throws Exception {
        ServiceReferenceType reference = citeTaskResultAccess.createServiceReference();
        assertNotNull(reference);
    }
    
    class SensorInstanceProviderSeam extends SensorInstanceProvider {
        ResultAccessDataServiceReference createResultAccess(SensorInstanceData sensorInstanceData) throws MissingSensorInformationException {
            return this.createResultAccessReference(sensorInstanceData);
        }
    }

}
