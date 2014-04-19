package pt.up.fe



class TwitterStreamingJobTests {
    static triggers = {
        simple repeatInterval: 5000l // execute job once in 5 seconds
    }

    def execute() {
        // execute job
    }
}
