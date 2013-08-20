require 'rack'
require 'sinatra/base'

class TestApp < Sinatra::Base
  get '/' do
    'Hello World from TestApp!'
  end

  post '/' do
     request.body.rewind
     b = request.body.read
     'Hello posted ' + b
  end
end



run TestApp.new