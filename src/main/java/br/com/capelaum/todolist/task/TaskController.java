package br.com.capelaum.todolist.task;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.capelaum.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private ITaskRepository taskRepository;

    @PostMapping("")
    public ResponseEntity<?> create(@RequestBody TaskModel taskModel, HttpServletRequest request) {
        var userId = request.getAttribute("userId");
        taskModel.setIdUser((UUID) userId);

        if (!this.validateTaskStartAtWithEndAt(taskModel)) {
            return ResponseEntity
                    .badRequest()
                    .body("A data de início deve ser antes da data de término");
        }

        var task = this.taskRepository.save(taskModel);

        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }

    @GetMapping("")
    public List<TaskModel> list(HttpServletRequest request) {
        var userId = request.getAttribute("userId");
        var userTasks = this.taskRepository.findByIdUser((UUID) userId);

        return userTasks;
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @RequestBody TaskModel taskModel,
            HttpServletRequest request,
            @PathVariable UUID id) {
        var task = this.taskRepository.findById(id).orElse(null);

        if (task == null) {
            return ResponseEntity
                    .badRequest()
                    .body("Tarefa não encontrada");
        }

        var userId = request.getAttribute("userId");

        if (!task.getIdUser().equals(userId)) {
            return ResponseEntity
                    .badRequest()
                    .body("Usuário não tem permissão para atulizar essa tarefa");
        }

        Utils.copyNonNullProperties(taskModel, task);

        if (!this.validateTaskStartAtWithEndAt(task)) {
            return ResponseEntity
                    .badRequest()
                    .body("A data de início deve ser antes da data de término");
        }

        var taskUpdated = this.taskRepository.save(task);

        return ResponseEntity.ok().body(taskUpdated);
    }

    public Boolean validateTaskStartAtWithEndAt(TaskModel taskModel) {
        if (taskModel.getStartAt().isAfter(taskModel.getEndAt())) {
            return false;
        }

        return true;
    }

}
