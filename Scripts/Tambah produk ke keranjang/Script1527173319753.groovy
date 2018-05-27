import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.checkpoint.CheckpointFactory as CheckpointFactory
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as MobileBuiltInKeywords
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testcase.TestCaseFactory as TestCaseFactory
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testdata.TestDataFactory as TestDataFactory
import com.kms.katalon.core.testobject.ObjectRepository as ObjectRepository
import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WSBuiltInKeywords
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUiBuiltInKeywords
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import groovy.json.JsonSlurper
import com.kms.katalon.core.testobject.impl.HttpTextBodyContent
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.testobject.ConditionType

/* Login */
def jsonSlurper = new JsonSlurper()
// panggil end point login
def response = WS.sendRequest(findTestObject('Login'))
// cek berhasil masuk atau ngga
if(WS.verifyResponseStatusCode(response, 200)){	
	// parsing respon login dari text ke json
	def objLogin = jsonSlurper.parseText(response.getResponseText())
	String token = objLogin.authentication.token
	println("Token : " + objLogin.authentication.token)
	
	// Create new ArrayList
	ArrayList<TestObjectProperty> HTTPHeader = new ArrayList<TestObjectProperty>()
	// Kirim token in HTTP header
	HTTPHeader.add(new TestObjectProperty('Content-Type', ConditionType.EQUALS, "application/json"))
	HTTPHeader.add(new TestObjectProperty('Authorization', ConditionType.EQUALS, "Bearer "+token))
	
	/* Ambil profile login */
	RequestObject profile = findTestObject('Profile')
	
	// set HTTP headerna
	profile.setHttpHeaderProperties(HTTPHeader)
	// kirim request
	resProfile = WS.sendRequest(profile)
	// parsing respon profile dari text ke json
	def objProfile = jsonSlurper.parseText(resProfile.getResponseText())
	
	/* Tambah product ke keranjang */
	RequestObject addCart = findTestObject('Post to keranjang')
	// set HTTP headerna
	addCart.setHttpHeaderProperties(HTTPHeader)
	// content bodyna
	String vsRequestBody = '{"ProductId":1,"BasketId":"'+objProfile.user.id+'","quantity":1}';
	addCart.setBodyContent(new HttpTextBodyContent(vsRequestBody))
	resAddCart = WS.sendRequest(addCart)
	// parsing respon add cart dari text ke json
	def objAddCart = jsonSlurper.parseText(resAddCart.getResponseText())
	
	assert objAddCart.status == "success"
	println(resAddCart.getResponseText())
}else{
	println(response.getResponseText())
}

//RequestObject req = findTestObject('Post to keranjang')
//req.setRestUrl("https://juice-shop.herokuapp.com/rest/user/whoami")
//
//String vsRequestBody = "{\"email\": \"' or email='jim@juice-sh.op' --\",  \"password\": \"pwku\"}";
//
//req.setBodyContent(new HttpTextBodyContent(vsRequestBody))
//def response = WS.sendRequest(req)
//println(response.getResponseText())



