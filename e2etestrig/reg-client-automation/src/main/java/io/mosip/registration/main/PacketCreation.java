package io.mosip.registration.main;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dto.RegistrationCenterDetailDTO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.service.config.GlobalParamService;
import io.mosip.registration.service.operator.UserOnboardService;
import io.mosip.registration.service.packet.PacketHandlerService;
import io.mosip.registration.util.CommonUtil;
import io.mosip.registration.util.ConstantValues;

@PropertySource("/RegClient/config.properties")
@ContextConfiguration(classes = { AppConfig.class })
public class PacketCreation extends AbstractTestNGSpringContextTests {

	@Autowired
	PacketHandlerService packetHandlerService;
	@Autowired
	Environment env;
	@Autowired
	UserOnboardService userOBservice;
	@Autowired
	CommonUtil commonUtil;

	/**
	 * Declaring CenterID,StationID global
	 */
	private static String centerID = null;
	private static String stationID = null;

	@Test
	public void createPacket() throws FileNotFoundException, IOException, ParseException {
		// Fetch value from PreId.json
		HashMap<String, String> preRegIDs = commonUtil.getPreRegIDs();

		// Common PreRequisite SetUp to create Packet
		ApplicationContext.map().put(RegistrationConstants.GPS_DEVICE_DISABLE_FLAG, ConstantValues.NO);
		SessionContext.getInstance().getMapObject().put(RegistrationConstants.IS_Child, true);
		centerID = userOBservice.getMachineCenterId().get(ConstantValues.CENTERIDLBL);
		stationID = userOBservice.getMachineCenterId().get(ConstantValues.STATIONIDLBL);
		SessionContext.getInstance().getUserContext().setUserId(ConstantValues.USERID);
		RegistrationCenterDetailDTO registrationCenter = new RegistrationCenterDetailDTO();
		registrationCenter.setRegistrationCenterId(centerID);
		SessionContext.getInstance().getUserContext().setRegistrationCenterDetailDTO(registrationCenter);
		ApplicationContext.map().put(RegistrationConstants.PACKET_STORE_LOCATION, "src/main/resources/packets");
		if (preRegIDs.get("RegClientPacketUinqueCBEFF").equalsIgnoreCase("YES")) {
			// Set CBEFF to UNIQUE
			ApplicationContext.map().put(RegistrationConstants.CBEFF_UNQ_TAG, ConstantValues.YES);
		} else {
			// Set CBEFF to UNIQUE & DUPLICATE
			ApplicationContext.map().put(RegistrationConstants.CBEFF_UNQ_TAG, ConstantValues.NO);
		}
		for (Entry<String, String> entry : preRegIDs.entrySet()) {
			if (!(entry.getKey().equalsIgnoreCase("RegClientPacketUinqueCBEFF"))) {
				// creating RegistrationDTO
				commonUtil.createRegistrationDTOObject(ConstantValues.REGISTRATIONCATEGORY, centerID, stationID);
				// Get Pre-RegistrationDTO
				RegistrationDTO preRegistrationDTO = commonUtil.getPreRegistrationDetails(entry.getValue());
				// Set RegistrationDTO to create packet
				RegistrationDTO regDTO = commonUtil.setRegistrationClientRegDTO(preRegistrationDTO);
				// create packet using RegistrationClient
				ResponseDTO response = packetHandlerService.handle(regDTO);
				logger.info(response.getSuccessResponseDTO().getMessage());
			}
		}

	}

}
