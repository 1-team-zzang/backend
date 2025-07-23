package com.example.calpick.domain.service;

public class MailMessageForm {
    public static class MailMessage {
        private final String title;
        private final String content;

        public MailMessage(String title, String content) {
            this.title = title;
            this.content = content;
        }

        public String getTitle() {
            return title;
        }

        public String getContent() {
            return content;
        }
    }

    public static MailMessage createAppointmentRequestMessage(String senderName, String appointmentTitle, String date, String redirectUrl) {
        String title = "[Calpick] " + senderName + "님이 약속을 신청하셨습니다.";
        String content = ""
                + "<p><strong>약속 제목:</strong> " + appointmentTitle + "</p>"
                + "<p><strong>날짜:</strong> " + date + "</p>"
                + "<p>" + senderName + "님이 약속을 신청하셨습니다.</p>"
                + "<p>👉 수락하거나 거절해 주세요!</p>"
                + "<a href='" + redirectUrl + "' "
                + "style='display:inline-block; margin-top:10px; padding:10px 20px; background-color:#4CAF50; color:white; text-decoration:none; border-radius:5px;'>"
                + "확인하러 가기</a>";
        return new MailMessage(title, content);
    }

    public static MailMessage createAppointmentRejectMessage(String senderName, String appointmentTitle, String date, String redirectUrl) {
        String title = "[Calpick] " + senderName + "님이 약속을 거절하셨습니다.";
        String content = ""
                + "<p><strong>약속 제목:</strong> " + appointmentTitle + "</p>"
                + "<p><strong>날짜:</strong> " + date + "</p>"
                + "<p>" + senderName + "님이 약속을 거절하셨습니다.</p>"
                + "<p>👉 다시 신청하시겠어요?</p>"
                + "<a href='" + redirectUrl + "' "
                + "style='display:inline-block; margin-top:10px; padding:10px 20px; background-color:#4CAF50; color:white; text-decoration:none; border-radius:5px;'>"
                + "페이지로 이동하기</a>";
        return new MailMessage(title, content);
    }

    public static MailMessage createAppointmentConfirmMessage(String senderName, String appointmentTitle, String date, String redirectUrl) {
        String title = "[Calpick] " + senderName + "님과의 약속이 확정되었습니다.";
        String content = ""
                + "<p><strong>약속 제목:</strong> " + appointmentTitle + "</p>"
                + "<p><strong>날짜:</strong> " + date + "</p>"
                + "<p>👉 내 캘린더도 공유해보세요</p>"
                + "<a href='" + redirectUrl + "' "
                + "style='display:inline-block; margin-top:10px; padding:10px 20px; background-color:#4CAF50; color:white; text-decoration:none; border-radius:5px;'>"
                + "페이지로 이동하기</a>";
        return new MailMessage(title, content);
    }

    public static MailMessage createFriendRequestMessage(String senderName,String redirectUrl) {
        String title = "[Calpick] " + senderName + "님이 친구 요청을 보냈습니다.";
        String content = ""
                + "<p>" + senderName + "님이 친구 요청을 보냈습니다.</p>"
                + "<p>👉 수락하거나 거절해 주세요!</p>"
                + "<a href='" + redirectUrl + "' "
                + "style='display:inline-block; margin-top:10px; padding:10px 20px; background-color:#4CAF50; color:white; text-decoration:none; border-radius:5px;'>"
                + "확인하러 가기</a>";
        return new MailMessage(title, content);
    }

    public static MailMessage createFriendRejectMessage(String senderName, String redirectUrl) {
        String title = "[Calpick] " + senderName + "님이 친구 요청을 거절하셨습니다.";
        String content = ""
                + "<p>" + senderName + "님이 친구 요청을 거절하셨습니다.</p>"
                + "<p>👉 다시 친구 요청하시겠어요?</p>"
                + "<a href='" + redirectUrl + "' "
                + "style='display:inline-block; margin-top:10px; padding:10px 20px; background-color:#4CAF50; color:white; text-decoration:none; border-radius:5px;'>"
                + "페이지로 이동하기</a>";
        return new MailMessage(title, content);
    }

    public static MailMessage createFriendConfirmMessage(String senderName, String redirectUrl) {
        String title = "[Calpick] " + senderName + "님과 친구가 되셨습니다.";
        String content = ""
                + "<p>" + senderName + "님과 친구가 되셨습니다.</p>"
                + "<p>👉 친구 목록 확인하기</p>"
                + "<a href='" + redirectUrl + "' "
                + "style='display:inline-block; margin-top:10px; padding:10px 20px; background-color:#4CAF50; color:white; text-decoration:none; border-radius:5px;'>"
                + "페이지로 이동하기</a>";
        return new MailMessage(title, content);
    }


}
