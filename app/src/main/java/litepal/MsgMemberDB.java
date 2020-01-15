package litepal;

import org.litepal.crud.DataSupport;

public class MsgMemberDB extends DataSupport {
    private Long messageId;
    private int members;

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public int getMembers() {
        return members;
    }

    public void setMembers(int members) {
        this.members = members;
    }
}
