/**
 * Sends a message to be logged by the server.
 * @param logger The name of the logger
 * @param body The message to log
 * @param level The severity of this log
 */
export function sendLog(logger: string, level: "trace" | "debug" | "info" | "warn" | "error", body: string) {
    const loggerName = encodeURIComponent(logger)
    fetch(`/log/${loggerName}/${level}`, {
        method: 'POST',
        body: body,
    })
}