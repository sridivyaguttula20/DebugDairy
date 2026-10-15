window.DEBUGDAIRY_API_BASE =
    window.location.host === "localhost:5500"
        || window.location.host === "127.0.0.1:5500"
        ? "http://localhost:8081"
        : "";
