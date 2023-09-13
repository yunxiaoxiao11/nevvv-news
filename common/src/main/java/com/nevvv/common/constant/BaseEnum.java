package com.nevvv.common.constant;

public enum BaseEnum {
    //文章图片类型
    NO_PIC(0, "文章图片类型-无图"),
    ONE_PIC(1, "文章图片类型-单图"),
    THREE_PIC(3, "文章图片类型-三图"),
    //文章状态
    ART_DRAFT(0, "文章状态-草稿"),
    ART_SUBMIT(1, "文章状态-提交(待审核)"),
    ART_AUDIT_FAILED(2, "文章状态-审核失败"),
    ART_HUMAN_REVIEW(3, "文章状态-人工审核"),
    ART_HUMAN_PASS(4, "文章状态-人工审核通过"),
    ART_PASS(8, "文章状态-审核通过(待发布)"),
    ART_PUBLISH(9, "文章状态-已发布"),
    //素材表图片是否被收藏
    IS_COLLECTION(true, "素材表图片被收藏"),
    IS_NOT_COLLECTION(false, "素材表图片未被收藏"),
    //新闻素材表图片引用类型
    FILE_PIC(0, "图片,主图引用"),
    FILE_VIDEO(1, "视频,内容引用"),
    //文章服务文章信息统计数量,点赞,收藏数等
    NEW_ARTICLE_NUM(0, "数量为0"),
    //文章标记 普通文章...
    FLAG_USUAL(0, "普通文章"),
    FLAG_HOT(1, "热点文章"),
    FLAG_TOP(2, "置顶文章"),
    //文章布局 无图,单图等
    LAYOUT_NO_PIC(0, "无图文章"),
    LAYOUT_SINGLE_PIC(1, "单图文章"),
    LAYOUT_NOPIC(3, "三图文章"),
    //是否可评论
    IS_COMMENT(true, "可以评论,转发,收藏,是否下架,已删除"),
    IS_NOT_COMMENT(false, "不可以评论,转发,收藏,是否下架,已删除"),
    //ocr解析结果键常量
    OCR_WORDS_RESULT(1, "words_result"),
    OCR_WORDS(1, "words"),
    //文章审核结果
    ART_PASS1(1, "pass"),
    //图片审核结果
    PIC_PASS(1, "pass"),
    PIC_REVIEW(2, "review"),
    PIC_BLOCK(0, "block"),
    LIKE_PRE(0, "like:"),
    READ_PRE(0, "read:"),
    IS_LIKE(0, "like"),
    IS_NOT_LIKE(1, "isNotLike"),
    FAN_LEVEL1(0, "正常"),
    FAN_LEVEL2(1, "潜力股"),
    FAN_LEVEL3(2, "勇士"),
    FAN_LEVEL4(3, "铁杆"),
    FAN_LEVEL5(4, "老铁"),
    IS_DISPLAY(true, "是"),
    IS_NOT_DISPLAY(false, "否"),
    follow_level1(0, "偶尔感兴趣"),
    follow_level2(1, "一般"),
    follow_level3(2, "经常"),
    follow_level4(3, "高度"),
    IS_NOTICE(true, "是"),
    IS_NOT_NOTICE(false, "否"),
    KAFKA_SOURCE(false, "TopicSource"),
    KAFKA_SINK(false, "TopicSink");
//    PIC_BLOCK(0, "block"),


    private final String name;
    private final Object value;

    <T> BaseEnum(T value, String name) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public <T> T getValue() {
        return (T) value;
    }
}