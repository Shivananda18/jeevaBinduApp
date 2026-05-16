package com.jeevabindu.data.local

import com.jeevabindu.data.local.entity.AlertEntity
import com.jeevabindu.data.local.entity.DonorEntity
import com.jeevabindu.data.local.entity.NotificationEntity
import com.jeevabindu.domain.model.AppNotification
import com.jeevabindu.domain.model.EmergencyAlert
import com.jeevabindu.domain.model.User

fun User.toEntity() = DonorEntity(
    uid = uid,
    name = name,
    age = age,
    gender = gender,
    bloodGroup = bloodGroup,
    phone = phone,
    address = address,
    district = district,
    lastDonationDateMillis = lastDonationDateMillis,
    availability = availability,
    profileImageUrl = profileImageUrl,
    lat = lat,
    lng = lng,
    isSuspended = isSuspended
)

fun DonorEntity.toUser() = User(
    uid = uid,
    name = name,
    age = age,
    gender = gender,
    bloodGroup = bloodGroup,
    phone = phone,
    address = address,
    district = district,
    lastDonationDateMillis = lastDonationDateMillis,
    availability = availability,
    profileImageUrl = profileImageUrl,
    lat = lat,
    lng = lng,
    isSuspended = isSuspended
)

fun EmergencyAlert.toEntity() = AlertEntity(
    id = id,
    requesterId = requesterId,
    requesterName = requesterName,
    bloodGroup = bloodGroup,
    hospitalName = hospitalName,
    patientCondition = patientCondition,
    unitsRequired = unitsRequired,
    contactNumber = contactNumber,
    locationText = locationText,
    district = district,
    lat = lat,
    lng = lng,
    createdAtMillis = createdAtMillis,
    active = active,
    status = status,
    acceptedCount = acceptedCount
)

fun AlertEntity.toAlert() = EmergencyAlert(
    id = id,
    requesterId = requesterId,
    requesterName = requesterName,
    bloodGroup = bloodGroup,
    hospitalName = hospitalName,
    patientCondition = patientCondition,
    unitsRequired = unitsRequired,
    contactNumber = contactNumber,
    locationText = locationText,
    district = district,
    lat = lat,
    lng = lng,
    createdAtMillis = createdAtMillis,
    active = active,
    status = status,
    acceptedCount = acceptedCount
)

fun AppNotification.toEntity() = NotificationEntity(
    id = id,
    userId = userId,
    title = title,
    body = body,
    type = type,
    relatedId = relatedId,
    read = read,
    createdAtMillis = createdAtMillis
)

fun NotificationEntity.toNotification() = AppNotification(
    id = id,
    userId = userId,
    title = title,
    body = body,
    type = type,
    relatedId = relatedId,
    read = read,
    createdAtMillis = createdAtMillis
)
