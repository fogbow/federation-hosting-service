package cloud.fogbow.fhs.api.http;

import org.springframework.web.bind.annotation.ControllerAdvice;
import cloud.fogbow.common.http.FogbowExceptionToHttpErrorConditionTranslator;

@ControllerAdvice
public class FhsExceptionToHttpErrorConditionTranslator extends FogbowExceptionToHttpErrorConditionTranslator {

}
