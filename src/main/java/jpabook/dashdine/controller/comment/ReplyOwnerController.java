package jpabook.dashdine.controller.comment;

import jpabook.dashdine.domain.user.UserRoleEnum;
import jpabook.dashdine.dto.request.comment.CreateReplyParam;
import jpabook.dashdine.dto.response.ApiResponseDto;
import jpabook.dashdine.dto.response.comment.ReplyForm;
import jpabook.dashdine.security.userdetails.UserDetailsImpl;
import jpabook.dashdine.service.comment.ReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static jpabook.dashdine.domain.user.UserRoleEnum.Authority.OWNER;

@RestController
@RequiredArgsConstructor
@Secured(OWNER)
public class ReplyOwnerController {

    private final ReplyService replyManagementService;

    @PostMapping("/reply")
    public ResponseEntity<ApiResponseDto> createReply(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                      @RequestBody CreateReplyParam param) {

        replyManagementService.createReply(userDetails.getUser(), param);

        return ResponseEntity.ok().body(new ApiResponseDto("답글 작성 완료", HttpStatus.OK.value()));
    }

    @GetMapping("/reply")
    public List<ReplyForm> readAllReplies(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return replyManagementService.readAllReplies(userDetails.getUser());
    }


}
