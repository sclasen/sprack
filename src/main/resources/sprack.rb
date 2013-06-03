require 'java'
require 'bundler/setup'
require 'rack'
require 'rack/rewindable_input'
require 'stringio'

module Sprack
  module RackServer
    class Builder
      def build(filename, host, port)
        rack_app, options_ignored = Rack::Builder.parse_file filename
        return SprayAdapter.new(rack_app, host, port)
      end
    end

    class SprayAdapter
      def initialize(app, host, port)
        @app = app
        @host = host
        @port = port.to_s
        @errors = java::lang::System::err.to_io #
        @logger = java::lang::System::out.to_io #
      end

      def call(request)

        rack_env = {
            'rack.version' => Rack::VERSION,
            'rack.multithread' => true,
            'rack.multiprocess' => false,
            'rack.input' => StringIO.new(request.input.utf8String),   #StringIO is a Rack Compliant IO I think
            'rack.errors' => @errors,
            'rack.logger' => @logger,
            'rack.url_scheme' => request.scheme,
            'REQUEST_METHOD' => request.method,
            'SCRIPT_NAME' => '',
            'PATH_INFO' => request.path,
            'REQUEST_PATH' => request.path,
            'REQUEST_URI' => request.path,
            'QUERY_STRING' => (request.query || ""),
            'SERVER_NAME' => @host,
            'SERVER_PORT' => @port,
            "SERVER_PROTOCOL"=>"HTTP/1.1",
            "SERVER_SOFTWARE" => "sprack"
        }

        rack_env['CONTENT_TYPE'] = request.content_type unless request.content_type.nil?
        rack_env['CONTENT_LENGTH']  = request.content_length unless request.content_length.nil?

        request.headers.each do |name, value|
          rack_env["HTTP_#{name.upcase.gsub(/-/,'_')}"] = value
        end

        rack_env['HTTP_ACCEPT'] = 'application/vnd.heroku+json; version=3' if rack_env['HTTP_ACCEPT'] == 'application/vnd.heroku+json'


        response_status, response_headers, response_body = @app.call(rack_env)

        spray_headers = []
        response_headers.each do |name, value|
          spray_headers << Java::spray::http::HttpHeaders::RawHeader.apply(name,value)
        end

        body = Java::java::util::Arrays.asList(response_body.map{|p| p.to_java_bytes }.to_java)
        headers = Java::java.util::Arrays.asList(spray_headers.to_java)

        [response_status.to_java, headers, body]

      end
    end


  end
end

