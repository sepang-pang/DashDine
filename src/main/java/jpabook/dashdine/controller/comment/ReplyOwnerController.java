package jpabook.dashdine.controller.comment;

import jpabook.dashdine.dto.request.comment.CreateReplyParam;
import jpabook.dashdine.dto.request.comment.UpdateReplyParam;
import jpabook.dashdine.dto.response.ApiResponseDto;
import jpabook.dashdine.dto.response.comment.ReplyForm;
import jpabook.dashdine.security.userdetails.UserDetailsImpl;
import jpabook.dashdine.service.comment.ReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping("/reply/{replyId}")
    public ResponseEntity<ApiResponseDto> updateReply(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                      @PathVariable("replyId")Long replyId,
                                                      @RequestBody UpdateReplyParam param) {

        replyManagementService.updateReply(userDetails.getUser(), replyId, param);

        return ResponseEntity.ok().body(new ApiResponseDto("답글 수정 완료", HttpStatus.OK.value()));
    }

    @PatchMapping("/reply/{replyId}")
    public ResponseEntity<ApiResponseDto> deleteReply(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                      @PathVariable("replyId")Long replyId) {

        replyManagementService.deleteReply(userDetails.getUser(), replyId);

        return ResponseEntity.ok().body(new ApiResponseDto("답글 삭제 완료", HttpStatus.OK.value()));
    }


}
