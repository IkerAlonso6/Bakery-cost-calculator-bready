package com.bakery.web.controller;

import com.bakery.application.dto.InputDTO;
import com.bakery.application.dto.UpdatePriceRequest;
import com.bakery.application.mapper.InputMapper;
import com.bakery.application.service.InputService;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/inputs")
public class InputController {

    private final InputService inputService;
    private final InputMapper inputMapper;

    public InputController(InputService inputService, InputMapper inputMapper) {
        this.inputService = inputService;
        this.inputMapper = inputMapper;
    }

    @PostMapping
    public ResponseEntity<InputDTO> create(@Valid @RequestBody InputDTO dto) {
        var created = inputService.createInput(
                dto.getName(),
                InputMapper.parseUnit(dto.getUnitOfMeasure()),
                dto.getPrice());
        return ResponseEntity.status(HttpStatus.CREATED).body(inputMapper.toDto(created));
    }

    @GetMapping
    public List<InputDTO> getAll() {
        return inputMapper.toDtoList(inputService.getAllInputs());
    }

    @GetMapping("/{id}")
    public InputDTO getById(@PathVariable Integer id) {
        return inputMapper.toDto(inputService.getInputById(id));
    }

    @PutMapping("/{id}/price")
    public InputDTO updatePrice(@PathVariable Integer id,
                                @Valid @RequestBody UpdatePriceRequest request) {
        return inputMapper.toDto(inputService.updateInputPrice(id, request.getPrice()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        inputService.deleteInput(id);
        return ResponseEntity.noContent().build();
    }
}
