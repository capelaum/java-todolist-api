package br.com.capelaum.todolist.task;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity(name = "tb_tasks")
public class TaskModel {

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;
    private String description;

    @Column(length = 50)
    private String title;
    private String priority;
    private LocalDateTime startAt;
    private LocalDateTime endAt;

    private UUID idUser;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public void setTitle(String title) throws Exception {
        if (title.length() > 50) {
            throw new Exception("O Título deve conter no máximo 50 caracteres");
        }

        this.title = title;
    }

    public void setStartAt(LocalDateTime startAt) throws Exception {
        var currentDate = LocalDateTime.now();

        if (currentDate.isAfter(startAt)) {
            throw new Exception("A data de início deve ser após a data atual");
        }

        this.startAt = startAt;
    }

    public void setEndAt(LocalDateTime endAt) throws Exception {
        var currentDate = LocalDateTime.now();

        if (currentDate.isAfter(endAt)) {
            throw new Exception("A data de término deve ser após a data atual");
        }

        this.endAt = endAt;
    }

}
