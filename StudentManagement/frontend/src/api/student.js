import request from './request'

export function getStudents(keyword) {
  return request.get('/students', { params: { keyword } })
}

export function getStats() {
  return request.get('/students/stats')
}

export function addStudent(data) {
  return request.post('/students', data)
}

export function updateStudent(id, data) {
  return request.put('/students/' + id, data)
}

export function deleteStudent(id) {
  return request.delete('/students/' + id)
}
