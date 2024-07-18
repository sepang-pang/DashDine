package jpabook.dashdine.service.comment;

import jpabook.dashdine.domain.comment.Reply;
import jpabook.dashdine.domain.comment.Review;
import jpabook.dashdine.domain.user.User;
import jpabook.dashdine.dto.request.comment.CreateReplyParam;
import jpabook.dashdine.dto.request.comment.UpdateReplyParam;
import jpabook.dashdine.dto.response.comment.ReplyDetailsForm;
import jpabook.dashdine.dto.response.comment.ReviewContentForm;
import jpabook.dashdine.repository.comment.ReplyRepository;
import jpabook.dashdine.service.comment.query.ReviewQueryService;
import jpabook.dashdine.service.user.query.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ReplyManagementService implements ReplyService {

    private final ReplyRepository replyRepository;
    private final ReviewQueryService reviewQueryService;
    private final UserQueryService userQueryService;

    @Override
    public void createReply(User user, CreateReplyParam param) {
        // 유저 조회
        User findUser = userQueryService.findUser(user.getId());

        // 리뷰 조회
        Review findReview = reviewQueryService.findOneReview(param.getReviewId());

        // 답글 생성
        Reply reply = Reply.createReply(findUser, findReview, param);

        replyRepository.save(reply);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReplyDetailsForm> readAllReplies(User user) {
        // 답글 조회
        List<ReplyDetailsForm> replyDetailsForms = replyRepository.findReplyFormsByUserId(user.getId());

        Map<Long, ReviewContentForm> reviewContentFormMap = getLongReviewContentFormMap(replyDetailsForms);

        replyDetailsForms.forEach(rf -> rf.setReviewContentForm(reviewContentFormMap.get(rf.getReviewId())));

        return replyDetailsForms;
    }

    @Override
    public void updateReply(User user, Long replyId, UpdateReplyParam param) {
        // 답글 조회
        Reply findReply = getReply(replyId);

        findReply.updateReply(user, param);
    }

    @Override
    public void deleteReply(User user, Long replyId) {
        Reply findReply = getReply(replyId);

        findReply.deleteReply(user);
    }

    private Map<Long, ReviewContentForm> getLongReviewContentFormMap(List<ReplyDetailsForm> replyDetailsForms) {
        List<Review> reviews = reviewQueryService.findAllReviews(getReviewIds(replyDetailsForms));

        return reviews.stream()
                .map(ReviewContentForm::new)
                .collect(Collectors.toMap(ReviewContentForm::getReviewId, Function.identity()));
    }

    private static List<Long> getReviewIds(List<ReplyDetailsForm> replyDetailsForms) {
        return replyDetailsForms.stream()
                .map(ReplyDetailsForm::getReviewId)
                .collect(Collectors.toList());
    }

    private Reply getReply(Long replyId) {
        return replyRepository.findReplyById(replyId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 항목입니다."));
    }
}
