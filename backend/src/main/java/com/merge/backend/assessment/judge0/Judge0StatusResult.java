package com.merge.backend.assessment.judge0;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Judge0StatusResult {

    private Status status;
    private String stdout;
    private String stderr;

    @JsonProperty("compile_output")
    private String compileOutput;

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public String getStdout() { return stdout; }
    public void setStdout(String stdout) { this.stdout = stdout; }

    public String getStderr() { return stderr; }
    public void setStderr(String stderr) { this.stderr = stderr; }

    public String getCompileOutput() { return compileOutput; }
    public void setCompileOutput(String compileOutput) { this.compileOutput = compileOutput; }

    public String errorOutput() {
        if (compileOutput != null && !compileOutput.isBlank()) return compileOutput;
        return stderr != null ? stderr : "";
    }

    public static class Status {
        private int id;
        private String description;

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
}
