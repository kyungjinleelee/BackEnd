package com.pickple.notification_service.application.service;

import com.pickple.common_module.exception.CommonErrorCode;
import com.pickple.common_module.exception.CustomException;
import com.pickple.notification_service.application.dto.ChannelCreateRespDto;
import com.pickple.notification_service.domain.model.Channel;
import com.pickple.notification_service.domain.repository.ChannelRepository;
import com.pickple.notification_service.exception.ChannelErrorCode;
import com.pickple.notification_service.infrastructure.messaging.events.EmailCreateRequestEvent;
import com.pickple.notification_service.presentation.request.ChannelCreateReqDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final EmailService emailService;

    private final ChannelRepository channelRepository;
    private final AuditorAware auditorProvider;


    // 알림 생성 - 이메일
    public void sendEmailNotification(@Valid EmailCreateRequestEvent event){
        emailService.sendEmail(event);
    }

    // 알림 삭제
    public void deleteNotification(){

    }

    // 알림 조회 - Email (user)


    // 알림 조회 - admin

    // 알림 채널 추가
    public ChannelCreateRespDto createChannel(ChannelCreateReqDto reqDto) {
        Channel channel = new Channel(reqDto);

        channelRepository.save(channel);

        return ChannelCreateRespDto.from(channel);
    }

    // 알림 채널 삭제
    public void deleteChannel(UUID channelId){
        Channel channel = channelRepository.findByIdAndIsDeleteIsFalse(channelId).orElseThrow(
                () -> new CustomException(ChannelErrorCode.CHANNEL_NOT_FOUND)
        );

        try {
            channel.delete(auditorProvider.toString());
            channelRepository.save(channel);
        }catch (Exception e){
            throw new CustomException(CommonErrorCode.DATABASE_ERROR);
        }
    }
}
