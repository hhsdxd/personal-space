import request from './request'

export function sendMessage(message) {
  return request.post('/ai/chat', { message })
}
