package ca.uhn.fhir.jaxrs.server.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.internal.MapPropertiesDelegate;
import org.glassfish.jersey.server.ContainerRequest;
import org.junit.Before;
import org.junit.Test;

import ca.uhn.fhir.jaxrs.server.test.TestJaxRsDummyPatientProvider;
import ca.uhn.fhir.jaxrs.server.test.TestJaxRsDummyPatientProviderDstu3;
import ca.uhn.fhir.jaxrs.server.util.JaxRsRequest;
import ca.uhn.fhir.jaxrs.server.util.JaxRsResponse;
import ca.uhn.fhir.rest.api.RequestTypeEnum;
import ca.uhn.fhir.rest.api.RestOperationTypeEnum;

public class JaxRsRequestDstu3Test {
	
	private static final String RESOURCE_STRING = "</Patient>";
	private static final String BASEURI = "http://baseuri";
	private static final String REQUESTURI = "http://baseuri/test";
	
	private JaxRsRequest details;
	private MultivaluedMap<String, String> queryParameters = new MultivaluedHashMap<String, String>();
	private ContainerRequest headers;
	private TestJaxRsDummyPatientProviderDstu3 provider;
	
	@Before
	public void setUp() throws URISyntaxException {
		details = createRequestDetails();
	}

	@Test
	public void testGetHeader() {
		String headerKey = "key";
		String headerValue = "location_value";
		String headerValue2 = "location_value_2";
		assertTrue(StringUtils.isBlank(details.getHeader(headerKey)));
		headers.header(headerKey, headerValue);
		assertEquals(headerValue, details.getHeader(headerKey));
		assertEquals(Arrays.asList(headerValue), details.getHeaders(headerKey));
		
		headers.header(headerKey, headerValue2);
		assertEquals(headerValue, details.getHeader(headerKey));
		assertEquals(Arrays.asList(headerValue, headerValue2), details.getHeaders(headerKey));
	}
	
	@Test
	public void testGetByteStreamRequestContents() {
		assertEquals(RESOURCE_STRING, new String(details.getByteStreamRequestContents()));
	}
	
	@Test
	public void testServerBaseForRequest() {
		assertEquals(BASEURI, new String(details.getServerBaseForRequest()));
	}
	
	@Test
	public void testGetResponse() {
		JaxRsResponse response = (JaxRsResponse) details.getResponse();
		assertEquals(details, response.getRequestDetails());
		assertTrue(response == details.getResponse());
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testGetReader() throws IOException {
		details.getReader();
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testGetInputStream() {
		details.getInputStream();
	}

	@Test
	public void testGetServerBaseForRequest() {
		assertEquals(JaxRsRequestDstu3Test.BASEURI, details.getFhirServerBase());
	}

	@Test
	public void testGetServer() {
		assertEquals(this.provider, details.getServer());
	}

	public JaxRsRequest createRequestDetails() throws URISyntaxException {
		//headers
		headers = new ContainerRequest(new URI(BASEURI), new URI(REQUESTURI), HttpMethod.GET, null, new MapPropertiesDelegate());
		
		//uri info
		UriInfo uriInfo = mock(UriInfo.class);
		when(uriInfo.getQueryParameters()).thenReturn(queryParameters);
		
		//mocks
		provider = spy(TestJaxRsDummyPatientProviderDstu3.class);
		doReturn(uriInfo).when(provider).getUriInfo();
		doReturn(BASEURI).when(provider).getBaseForRequest();
		doReturn(BASEURI).when(provider).getBaseForServer();
		doReturn(headers).when(provider).getHeaders();
		
		return new JaxRsRequest(provider, RESOURCE_STRING, RequestTypeEnum.GET, RestOperationTypeEnum.HISTORY_TYPE);
	}	

}
