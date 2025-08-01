class ApiError extends Error{
    constructor(
        statusCode,
        message = "Something went wrong",
        error = [],
        stack =""
    ){
        super(message);
        this.statusCode = statusCode;
        this.error = error;
        this.stack = stack;
        this.success = false;
        this.errors = this.errors

        if(stack){
            this.stack = stack;
        }else{
            Error.captureStackTrace(this, this.constructor);
        }
    }
}

export {ApiError}