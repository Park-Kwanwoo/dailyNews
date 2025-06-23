package dev.park.dailynews.aop;

import dev.park.dailynews.dto.request.SubjectRequest;
import dev.park.dailynews.dto.request.SubjectUpdateRequest;
import dev.park.dailynews.exception.BadKeywordException;
import dev.park.dailynews.infra.openai.CustomOpenAIClient;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class SubjectValidationAspect {

    private final CustomOpenAIClient client;

    @Around("(@annotation(org.springframework.web.bind.annotation.PostMapping) || @annotation(org.springframework.web.bind.annotation.PatchMapping))" +
            " && (execution(* *..SubjectController.save(..)) || execution(* *..SubjectController.update(..)))")
    public Object validateKeyword(ProceedingJoinPoint joinPoint) throws Throwable {
        boolean isValid = true;
        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof SubjectRequest) {
                String keyword = ((SubjectRequest) arg).getKeyword();
                isValid = client.validateKeyword(keyword);
            } else if (arg instanceof SubjectUpdateRequest){
                String keyword = ((SubjectUpdateRequest) arg).getKeyword();
                isValid = client.validateKeyword(keyword);
            }
            if (!isValid) {
                throw new BadKeywordException();
            }
        }
        return joinPoint.proceed();
    }
}
